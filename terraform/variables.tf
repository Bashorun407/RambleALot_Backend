variable "aws_region" {
  description = "AWS deployment region"
  type        = string
  default     = "us-east-2" # Update to your actual region if different
}

variable "aws_vpc" {
  description = "AWS VPC"
  type        = string
  default     = "vpc-08b0792f0989ff5e0" # Update to your actual vpc if different
}

variable "aws_subnet" {
  description = "AWS subnet"
  type        = string
  default     = "subnet-020e6c74deb553754" # Update to your actual subnet if different
}

variable "key_pair_name" {
  description = "connection key pair"
  type        = string
  default     = "ramble2-keypair"
}

variable "db_password" {
  description = "Password for the db"
  type        = string
  sensitive   = true
}