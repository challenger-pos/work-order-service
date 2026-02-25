terraform {
  backend "s3" {
    bucket  = "tf-state-challenge-bucket"
    region  = "us-east-2"
    # key será passado dinamicamente via -backend-config
    # Exemplo: -backend-config=key=v4/work-order-service/dev/terraform.tfstate
  }
}