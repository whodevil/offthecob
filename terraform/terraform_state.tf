terraform {
  backend "s3" {
    bucket = "otc-tf"
    key    = "offthecob/terraform.tfstate"
    region = "us-west-2"
  }
}
