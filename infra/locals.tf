locals {
  # Remote States Paths
  rds_state_path              = "v4/rds-os/${var.environment}/terraform.tfstate"
  infra_kubernetes_state_path = "v4/kubernetes/${var.environment}/terraform.tfstate"

  # AWS Region and Configuration
  region              = "us-east-2"
  aws_sqs_endpoint    = "https://sqs.us-east-2.amazonaws.com/"
  aws_region_static   = "us-east-2"

  # SQS Queue Names (8 filas necessárias para integração)
  sqs_queues = {
    payment_failure        = "payment-response-failure-queue"
    payment_request        = "payment-request-queue"
    payment_success        = "payment-response-success-queue"
    stock_approved         = "work-order-stock-approved"
    stock_cancel_requested = "work-order-stock-cancel-requested"
    stock_failed           = "stock-failed-queue"
    stock_request          = "work-order-stock-requested"
    stock_reserved         = "stock-reserved-queue"
  }

  # Spring Cloud AWS Configuration (constantes)
  spring_cloud_aws = {
    visibility_timeout = "300000"  # 5 minutos
    wait_time_seconds  = "20"      # Long polling
    max_receive_count  = "10"      # Máximo de recebimentos SQS
  }

  # Environment-specific App Configuration
  app_configs = {
    dev = {
      datadog_enabled = true
      replicas        = 1
      log_level       = "DEBUG"
    }
    homologation = {
      datadog_enabled = true
      replicas        = 2
      log_level       = "INFO"
    }
    production = {
      datadog_enabled = true
      replicas        = 3
      log_level       = "WARN"
    }
  }

  # Current environment configuration
  current_env = local.app_configs[var.environment]
}