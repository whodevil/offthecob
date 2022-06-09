module "api" {
  source         = "terraform-aws-modules/lambda/aws"
  function_name  = "api-${terraform.workspace}"
  create_package = false
  image_uri      = var.API_IMAGE
  package_type   = "Image"
  publish        = true

  allowed_triggers = {
    AllowExecutionFromAPIGateway = {
      service    = "apigateway"
      source_arn = "${module.api_gateway.apigatewayv2_api_execution_arn}/*/*"
    }
  }
}
