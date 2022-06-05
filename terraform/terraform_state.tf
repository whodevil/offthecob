terraform {
  backend "s3" {
    bucket = var.TERRAFORM_STATE_BUCKET
    key    = "theagainagain/terraform.tfstate"
    region = "us-west-2"
  }
}
