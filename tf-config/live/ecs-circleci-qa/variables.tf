variable "aws_access_key" {}
variable "aws_secret_key" {}
variable "aws_region" {
  default = "us-east-1"
  description = "AWS region e.g. us-east-1 (Please specify a region supported by the Fargate launch type)"
}
