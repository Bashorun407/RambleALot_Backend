variable "aws_region" {
  description = "AWS deployment region"
  type        = string
  #default     = "us-east-2" # Update to your actual region if different
  default     = "us-east-1" #CIL region
}

variable "aws_vpc" {
  description = "AWS VPC"
  type        = string
  #default     = "vpc-08b0792f0989ff5e0" # Update to your actual vpc if different (personal)
  default     = "vpc-0a8db87277c7365bf" # CIL vpc
}

variable "aws_subnet" {
  description = "AWS subnet"
  type        = string
  #default     = "subnet-020e6c74deb553754" # Update to your actual subnet if different
  default     = "subnet-03bc28942d1bd9dc6" #CIL subnet
}

variable "key_pair_name" {
  description = "connection key pair"
  type        = string
  #default     = "ramble2-keypair"
  default     = "seyikeypair" #CIL keypair
}

variable "db_password" {
  description = "Password for the db"
  type        = string
  sensitive   = true
}

variable "s3_bucket_name" {
  description = "Globally unique name for the S3 images bucket"
  type        = string
  default     = "ramblealot-assets-olusheyi-2026" # Change this to be completely unique
}