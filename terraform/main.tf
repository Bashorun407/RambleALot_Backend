terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Fetch the latest Ubuntu 24.04 LTS Image
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-amd64-server-*"]
  }
}

# Fetch your default VPC
data "aws_vpc" "default" {
  default = true
}

# Fetch all subnets inside that VPC
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Security Group
resource "aws_security_group" "ramblealot_sg" {
  name        = "ramblealot-sg-iac" #Personal aws account
  description = "Allow SSH and HTTP traffic for MVP"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH from anywhere"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP for Spring Boot Backend"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# EC2 Instance
resource "aws_instance" "app_server" {
  ami                         = data.aws_ami.ubuntu.id
  #instance_type               = "t3.micro" #Personal aws account
  instance_type                = "t2.micro"
  key_name                    = var.key_pair_name
  vpc_security_group_ids      = [aws_security_group.ramblealot_sg.id]
  subnet_id                   = data.aws_subnets.default.ids[0]
  associate_public_ip_address = true

  user_data = <<-EOF
              #!/bin/bash
              apt-get update -y
              apt-get install -y docker.io
              systemctl start docker
              systemctl enable docker

              docker run -d --name ramblealot-db \
                -e POSTGRES_DB=ramblealot_db \
                -e POSTGRES_USER=postgres \
                -e POSTGRES_PASSWORD=${var.db_password} \
                -p 5432:5432 \
                postgis/postgis:16-3.4
              EOF

  tags = {
    Name = "dev02-node-iac"
  }
}

# ==========================================
# NEW: S3 BUCKET FOR IMAGES
# ==========================================
resource "aws_s3_bucket" "image_assets" {
  bucket = var.s3_bucket_name # Moved to variables so you can ensure a globally unique name
}

# Allow public read access to the images
resource "aws_s3_bucket_public_access_block" "public_access" {
  bucket                  = aws_s3_bucket.image_assets.id
  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "public_read" {
  bucket     = aws_s3_bucket.image_assets.id
  depends_on = [aws_s3_bucket_public_access_block.public_access]
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "PublicReadGetObject"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.image_assets.arn}/*"
      }
    ]
  })
}

# ==========================================
# NEW: AWS LAMBDA & IAM ROLES
# ==========================================
# IAM Role for Lambda
resource "aws_iam_role" "lambda_exec_role" {
  name = "ramblealot_lambda_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

# Allow Lambda to write logs and read/write to the S3 bucket
resource "aws_iam_role_policy" "lambda_s3_policy" {
  name = "lambda_s3_permissions"
  role = aws_iam_role.lambda_exec_role.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "logs:CreateLogGroup",
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ]
        Effect   = "Allow"
        Resource = "arn:aws:logs:*:*:*"
      },
      {
        Action = [
          "s3:GetObject",
          "s3:PutObject"
        ]
        Effect   = "Allow"
        Resource = "${aws_s3_bucket.image_assets.arn}/*"
      }
    ]
  })
}

# The Lambda Function
resource "aws_lambda_function" "image_processor" {
  filename      = "lambda_function.zip" # You must create this placeholder file before running apply
  function_name = "ProcessLocationImages"
  role          = aws_iam_role.lambda_exec_role.arn
  handler       = "index.handler"
  runtime       = "nodejs20.x" # Or python3.10 depending on what you prefer for the hackathon
}

# Allow S3 to trigger the Lambda
resource "aws_lambda_permission" "allow_s3" {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.image_processor.arn
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.image_assets.arn
}

# The S3 Event Trigger
resource "aws_s3_bucket_notification" "bucket_notification" {
  bucket = aws_s3_bucket.image_assets.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.image_processor.arn
    events              = ["s3:ObjectCreated:*"]
  }

  depends_on = [aws_lambda_permission.allow_s3]
}