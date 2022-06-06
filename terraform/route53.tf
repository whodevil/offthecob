locals {
  domain_name_prefix = terraform.workspace == "production" ? "www" : terraform.workspace
  domain_name = "${local.domain_name_prefix}.offthecob.info"
}

resource "aws_route53_record" "www" {
  zone_id = var.ZONE_ID
  name    = local.domain_name
  type    = "A"
  allow_overwrite = true
  alias {
    name = aws_cloudfront_distribution.www_s3_distribution.domain_name
    zone_id = aws_cloudfront_distribution.www_s3_distribution.hosted_zone_id
    evaluate_target_health = false
  }
}
