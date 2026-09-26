output "instance_public_ip" {
  description = "Update your GitHub EC2_HOST secret with this IP"
  value       = aws_instance.app_server.public_ip
}

output "instance_private_ip" {
  description = "Update the Spring Datasource URL in aws-deploy.yml with this IP"
  value       = aws_instance.app_server.private_ip
}