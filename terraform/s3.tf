resource "aws_s3_bucket" "website" {
  bucket = "theagainagain-${terraform.workspace}"
}

data "aws_iam_policy_document" "website" {
  statement {
    actions = [
      "s3:GetObject"
    ]
    principals {
      identifiers = ["*"]
      type        = "AWS"
    }
    resources = [
      "arn:aws:s3:::theagainagain-${terraform.workspace}/*"
    ]
  }
}

resource "aws_s3_bucket_policy" "website" {
  bucket = aws_s3_bucket.website.id
  policy = data.aws_iam_policy_document.website.json
}

resource "aws_s3_bucket_cors_configuration" "website" {
  bucket = aws_s3_bucket.website.id
  cors_rule {
    allowed_headers = ["Authorization", "Content-Length"]
    allowed_methods = ["GET"]
    allowed_origins = ["https://${local.domain_name}"]
    max_age_seconds = 3000
  }
}

resource "aws_s3_bucket_website_configuration" "website" {
  bucket = aws_s3_bucket.website.id
  index_document {
    suffix = "index.html"
  }
  error_document {
    key = "index.html"
  }
}

resource "aws_s3_bucket_acl" "website" {
  bucket = aws_s3_bucket.website.id
  acl    = "public-read"
}

resource "aws_s3_object" "html" {
  for_each = fileset("public/", "**/*.html")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "text/html"
}

resource "aws_s3_object" "svg" {
  for_each = fileset("public/", "**/*.svg")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "image/svg+xml"
}

resource "aws_s3_object" "css" {
  for_each = fileset("public/", "**/*.css")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "text/css"
}

resource "aws_s3_object" "js" {
  for_each = fileset("public/", "**/*.js")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "application/javascript"
}

resource "aws_s3_object" "images" {
  for_each = fileset("public/", "**/*.png")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "image/png"
}

resource "aws_s3_object" "jpegs" {
  for_each = fileset("public/", "**/*.jpg")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "image/jpeg"
}

resource "aws_s3_object" "woff2" {
  for_each = fileset("public/", "**/*.woff2")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "font/woff2"
}

resource "aws_s3_object" "json" {
  for_each = fileset("public/", "**/*.json")

  bucket       = aws_s3_bucket.website.bucket
  key          = each.value
  source       = "public/${each.value}"
  etag         = filemd5("public/${each.value}")
  content_type = "application/json"
}
