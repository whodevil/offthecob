resource "aws_acm_certificate" "ssl_certificate" {
  provider                  = aws.us-east-1
  domain_name               = local.domain_name
  subject_alternative_names = ["*.${local.domain_name}", local.domain_name]
  validation_method         = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "cert_validation" {
  provider                = aws.us-east-1
  certificate_arn         = aws_acm_certificate.ssl_certificate.arn
  validation_record_fqdns = [for record in aws_route53_record.validation : record.fqdn]
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
