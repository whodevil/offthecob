module "api" {
  source                                  = "terraform-aws-modules/lambda/aws"
  function_name                           = "api-${terraform.workspace}"
  create_package                          = false
  image_uri                               = var.API_IMAGE
  package_type                            = "Image"
  create_current_version_allowed_triggers = false
  memory_size                             = 1024

  allowed_triggers = {
    AllowExecutionFromAPIGateway = {
      service    = "apigateway"
      source_arn = "${module.api_gateway.apigatewayv2_api_execution_arn}/*/*"
    }
  }

  environment_variables = {
    SERVICE_VERSION = var.TAG
    CORS_ORIGIN = "https://${local.domain_name}"
  }
}
