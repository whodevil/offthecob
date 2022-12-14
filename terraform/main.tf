provider "aws" {
  region = "us-west-2"
}

provider "aws" {
  alias  = "us-east-1"
  region = "us-east-1"
}

locals {
  root_host_name     = "offthecob.info"
  domain_name_prefix = terraform.workspace == "production" ? "www" : terraform.workspace
  domain_name        = "${local.domain_name_prefix}.${local.root_host_name}"
}
