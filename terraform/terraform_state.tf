terraform {
  backend "s3" {
    bucket = "otc-tf"
    key    = "theagainagain/terraform.tfstate"
    region = "us-west-2"
  }
}
