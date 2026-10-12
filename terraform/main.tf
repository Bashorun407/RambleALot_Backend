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

# Security Group
resource "aws_security_group" "ramblealot_sg" {
  name        = "ramble-sg-iac"
  description = "Allow SSH and HTTP traffic for MVP"
  vpc_id      = var.aws_vpc

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
  instance_type               = "t3.micro"
  key_name                    = var.key_pair_name
  vpc_security_group_ids      = [aws_security_group.ramblealot_sg.id]
  subnet_id                   = var.aws_subnet
  associate_public_ip_address = true

  # Automatically configure server and database on boot
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