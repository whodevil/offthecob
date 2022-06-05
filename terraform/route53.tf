data "aws_route53_zone" "site" {
  name         = var.ROOT_ZONE_NAME
}

resource "aws_route53_record" "www" {
  zone_id = data.aws_route53_zone.site.zone_id
  name    = var.DOMAIN_NAME
  type    = "A"
  alias {
    name = aws_s3_bucket.website.website_domain
    zone_id = aws_s3_bucket.website.hosted_zone_id
    evaluate_target_health = false
  }
}