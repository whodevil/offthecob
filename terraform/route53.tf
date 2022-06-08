resource "aws_route53_record" "www" {
  zone_id         = var.ZONE_ID
  name            = local.domain_name
  type            = "A"
  allow_overwrite = true
  alias {
    name                   = aws_cloudfront_distribution.www_s3_distribution.domain_name
    zone_id                = aws_cloudfront_distribution.www_s3_distribution.hosted_zone_id
    evaluate_target_health = false
  }
}

resource "aws_route53_record" "validation" {
  for_each = {
  for dvo in aws_acm_certificate.ssl_certificate.domain_validation_options : dvo.domain_name => {
    name   = dvo.resource_record_name
    record = dvo.resource_record_value
    type   = dvo.resource_record_type
  }
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = var.ZONE_ID
}
