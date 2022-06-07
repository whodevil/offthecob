resource "aws_acm_certificate" "ssl_certificate" {
  provider = aws.acm_provider
  domain_name               = local.domain_name
  subject_alternative_names = ["*.${local.domain_name}", local.domain_name]
  validation_method         = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "cert_validation" {
  provider = aws.acm_provider
  certificate_arn         = aws_acm_certificate.ssl_certificate.arn
  validation_record_fqdns =  [for record in aws_route53_record.validation : record.fqdn]
}

resource "aws_acm_certificate" "api_ssl_certificate" {
  domain_name               = local.api_name
  subject_alternative_names = ["*.${local.api_name}", local.api_name]
  validation_method         = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "api_cert_validation" {
  certificate_arn         = aws_acm_certificate.api_ssl_certificate.arn
  validation_record_fqdns =  [for record in aws_route53_record.api_validation : record.fqdn]
}
