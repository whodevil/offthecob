module "acm_www" {
  providers = {
    aws = aws.us-east-1
  }
  source  = "terraform-aws-modules/acm/aws"
  version = "~> 3.0"

  wait_for_validation = true

  domain_name               = local.domain_name
  zone_id                   = var.ZONE_ID
  subject_alternative_names = ["*.${local.domain_name}", local.domain_name]
  tags = {
    environment = terraform.workspace
  }
}

module "acm" {
  source  = "terraform-aws-modules/acm/aws"
  version = "~> 3.0"

  wait_for_validation = true

  domain_name               = local.api_name
  zone_id                   = var.ZONE_ID
  subject_alternative_names = ["*.${local.api_name}", local.api_name]
  tags = {
    environment = terraform.workspace
  }
}
