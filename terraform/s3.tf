resource "aws_s3_bucket" "website" {
  bucket = "theagainagain-${terraform.workspace}"
}

#resource "aws_s3_bucket_cors_configuration" "website" {
#  bucket = aws_s3_bucket.website.id
#  cors_rule {
#    allowed_headers = ["Authorization", "Content-Length"]
#    allowed_methods = ["GET"]
#    allowed_origins = ["https://${var.DOMAIN_NAME}"]
#    max_age_seconds = 3000
#  }
#}

resource "aws_s3_bucket_website_configuration" "website" {
  bucket = aws_s3_bucket.website.id
  index_document {
    suffix = "index.html"
  }
}

resource "aws_s3_bucket_acl" "website" {
  bucket = aws_s3_bucket.website.id
  acl    = "public-read"
}

resource "aws_s3_object" "html" {
  for_each = fileset("build/", "**/*.html")

  bucket = aws_s3_bucket.website.bucket
  key    = each.value
  source = "build/${each.value}"
  etag   = filemd5("build/${each.value}")
  content_type = "text/html"
}

resource "aws_s3_object" "svg" {
  for_each = fileset("build/", "**/*.svg")

  bucket = aws_s3_bucket.website.bucket
  key    = each.value
  source = "build/${each.value}"
  etag   = filemd5("build/${each.value}")
  content_type = "image/svg+xml"
}

resource "aws_s3_object" "css" {
  for_each = fileset("build/", "**/*.css")

  bucket = aws_s3_bucket.website.bucket
  key    = each.value
  source = "build/${each.value}"
  etag   = filemd5("build/${each.value}")
  content_type = "text/css"
}

resource "aws_s3_object" "js" {
  for_each = fileset("build/", "**/*.js")

  bucket = aws_s3_bucket.website.bucket
  key    = each.value
  source = "build/${each.value}"
  etag   = filemd5("build/${each.value}")
  content_type = "application/javascript"
}

resource "aws_s3_object" "images" {
  for_each = fileset("build/", "**/*.png")

  bucket = aws_s3_bucket.website.bucket
  key    = each.value
  source = "build/${each.value}"
  etag   = filemd5("build/${each.value}")
  content_type = "image/png"
}

resource "aws_s3_object" "json" {
  for_each = fileset("build/", "**/*.json")

  bucket = aws_s3_bucket.website.bucket
  key    = each.value
  source = "build/${each.value}"
  etag   = filemd5("build/${each.value}")
  content_type = "application/json"
}