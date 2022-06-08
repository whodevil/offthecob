resource "aws_cloudwatch_log_group" "logs" {
  name = "apigateway-${terraform.workspace}"
}
