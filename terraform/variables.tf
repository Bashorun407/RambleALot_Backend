variable "aws_region" {
  description = "AWS deployment region"
  type        = string
  default     = "us-east-1" # Update to your actual region if different
}

variable "key_pair_name" {
  description = "connection key pair"
  type        = string
  default     = "ramblealot-keypair"
}

variable "db_password" {
  description = "Password for the db"
  type        = string
  sensitive   = true
}