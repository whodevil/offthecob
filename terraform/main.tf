provider "aws" {
  region = "us-west-2"
}

provider "aws" {
  alias = "acm_provider"
  region = "us-east-1"
}
