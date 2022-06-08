resource "aws_apigatewayv2_api" "api" {
  name          = "offthecob-${terraform.workspace}"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_stage" "graphql" {
  api_id = aws_apigatewayv2_api.api.id
  name   = "graphql"
  auto_deploy = true
}
