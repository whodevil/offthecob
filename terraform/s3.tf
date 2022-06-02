resource "aws_s3_bucket" "theagainagain_ui" {
  bucket = "theagainagain-${terraform.workspace}"
}

resource "aws_s3_bucket_acl" "theagainagain_ui" {
  bucket = aws_s3_bucket.theagainagain_ui.id
  acl    = "public-read"
}
