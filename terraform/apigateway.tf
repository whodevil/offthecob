resource "aws_apigatewayv2_api" "api" {
  name          = "offthecob-${terraform.workspace}"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "graphql" {
  api_id = aws_apigatewayv2_api.api.id
  name   = "graphql"
  auto_deploy = true
}

resource "aws_apigatewayv2_domain_name" "api" {
  domain_name = local.api_name
  domain_name_configuration {
    certificate_arn = module.acm.acm_certificate_arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }
}

resource "aws_apigatewayv2_api_mapping" "example" {
  api_id      = aws_apigatewayv2_api.api.id
  domain_name = aws_apigatewayv2_domain_name.api.id
  stage       = aws_apigatewayv2_stage.graphql.id
}

resource "aws_apigatewayv2_route" "graphql" {
  api_id    = aws_apigatewayv2_api.api.id
  route_key = "POST /{proxy+}"

  target = "integrations/${aws_apigatewayv2_integration.graphql.id}"
}

resource "aws_apigatewayv2_integration" "graphql" {
  api_id           = aws_apigatewayv2_api.api.id
  integration_type = "AWS"

  connection_type           = "INTERNET"
  content_handling_strategy = "CONVERT_TO_TEXT"
  description               = "Lambda example"
  integration_method        = "POST"
  integration_uri           = module.api.lambda_function_invoke_arn
  passthrough_behavior      = "WHEN_NO_MATCH"
}
