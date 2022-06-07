resource "aws_apigatewayv2_api" "api" {
  name          = "api-offthecob-${terraform.workspace}"
  protocol_type = "HTTP"
  target        = module.api.lambda_function_arn
}

resource "aws_lambda_permission" "api_gw" {
  statement_id  = "AllowExecutionFromAPIGateway"
  action        = "lambda:InvokeFunction"
  function_name = module.api.lambda_function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_apigatewayv2_api.api.execution_arn}/*/*"
}

resource "aws_apigatewayv2_stage" "lambda" {
  api_id = aws_apigatewayv2_api.api.id

  name        = "graphql"
  auto_deploy = true
}

resource "aws_apigatewayv2_integration" "api" {
  api_id = aws_apigatewayv2_api.api.id

  integration_uri    = module.api.lambda_function_invoke_arn
  integration_type   = "AWS_PROXY"
  integration_method = "POST"
}

resource "aws_apigatewayv2_route" "hello_world" {
  api_id = aws_apigatewayv2_api.api.id

  route_key = "POST /graphql"
  target    = "integrations/${aws_apigatewayv2_integration.api.id}"
}

resource "aws_api_gateway_domain_name" "api" {
  certificate_arn = aws_acm_certificate_validation.api_cert_validation.certificate_arn
  domain_name     = local.api_name
}
