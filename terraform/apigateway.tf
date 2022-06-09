module "api_gateway" {
  source = "terraform-aws-modules/apigateway-v2/aws"

  name          = "api-otc-${terraform.workspace}"
  description   = "otc api ${terraform.workspace}"
  protocol_type = "HTTP"

  cors_configuration = {
    allow_headers = ["content-type", "x-amz-date", "authorization", "x-api-key", "x-amz-security-token", "x-amz-user-agent"]
    allow_methods = ["*"]
    allow_origins = ["*"]
  }

  domain_name                 = local.api_name
  domain_name_certificate_arn = module.acm.acm_certificate_arn

  default_stage_access_log_destination_arn = aws_cloudwatch_log_group.logs.arn
  default_stage_access_log_format          = "{ \"sourceIp\":\"$context.identity.sourceIp\", \"requestTime\":\"$context.requestTime\", \"method\":\"$context.httpMethod\", \"routeKey\":\"$context.routeKey\", \"protocol\":\"$context.protocol\", \"status\":\"$context.status\", \"responseLength\":\"$context.responseLength\", \"requestId\":\"$context.requestId\", \"integrationErrorMessage\":\"$context.integrationErrorMessage\"}"

  default_route_settings = {
    detailed_metrics_enabled = true
    throttling_burst_limit   = 100
    throttling_rate_limit    = 100
  }

  integrations = {
    "POST /graphql" = {
      lambda_arn             = module.api.lambda_function_arn
      payload_format_version = "2.0"
      timeout_milliseconds   = 12000
    }

    "OPTIONS /graphql" = {
      lambda_arn             = module.api.lambda_function_arn
      payload_format_version = "2.0"
      timeout_milliseconds   = 12000
    }
  }

  tags = {
    environment = terraform.workspace
  }
}
