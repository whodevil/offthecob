module "api" {
  source         = "terraform-aws-modules/lambda/aws"
  function_name  = "api-${terraform.workspace}"
  create_package = false
  image_uri      = var.API_IMAGE
  package_type   = "Image"
}
