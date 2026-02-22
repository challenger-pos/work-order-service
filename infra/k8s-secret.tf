resource "kubernetes_secret" "app_secret" {
  metadata {
    name      = "${var.app_name}-secret"
    namespace = kubernetes_namespace.challengeone.metadata[0].name
  }

  type = "Opaque"

  # O Terraform Provider automaticamente encoda em base64
  # NÃO use base64encode() aqui - deixe o Provider fazer
  data = {
    # ===== DATABASE =====
    SPRING_DATASOURCE_USERNAME = data.terraform_remote_state.rds.outputs.db_username
    SPRING_DATASOURCE_PASSWORD = var.db_password

    # ===== AWS CREDENTIALS (SQS & Other Services) =====
    AWS_ACCESS_KEY_ID     = var.aws_access_key
    AWS_SECRET_ACCESS_KEY = var.aws_secret_key
    SPRING_CLOUD_AWS_CREDENTIALS_ACCESS_KEY = var.aws_access_key
    SPRING_CLOUD_AWS_CREDENTIALS_SECRET_KEY = var.aws_secret_key

    # ===== AWS REGION & ENDPOINT =====
    AWS_REGION                        = local.region
    SPRING_CLOUD_AWS_REGION_STATIC    = local.aws_region_static
    SPRING_CLOUD_AWS_ENDPOINT         = local.aws_sqs_endpoint
    SPRING_CLOUD_AWS_SQS_ENDPOINT     = local.aws_sqs_endpoint

    # ===== SQS QUEUE NAMES (8 FILAS) =====
    AWS_SQS_QUEUE_PAYMENT-FAILURE        = local.sqs_queues.payment_failure
    AWS_SQS_QUEUE_PAYMENT-REQUEST        = local.sqs_queues.payment_request
    AWS_SQS_QUEUE_PAYMENT-SUCCESS        = local.sqs_queues.payment_success
    AWS_SQS_QUEUE_STOCK-APPROVED         = local.sqs_queues.stock_approved
    AWS_SQS_QUEUE_STOCK-CANCEL           = local.sqs_queues.stock_cancel_requested
    AWS_SQS_QUEUE_STOCK-FAILED           = local.sqs_queues.stock_failed
    AWS_SQS_QUEUE_STOCK-REQUEST          = local.sqs_queues.stock_request
    AWS_SQS_QUEUE_STOCK-RESERVED         = local.sqs_queues.stock_reserved

    # ===== SPRING CLOUD AWS SQS CONFIG =====
    SPRING_CLOUD_AWS_SQS_LISTENER_VISIBILITY_TIMEOUT = local.spring_cloud_aws.visibility_timeout
    SPRING_CLOUD_AWS_SQS_LISTENER_WAIT_TIME_SECONDS  = local.spring_cloud_aws.wait_time_seconds
    SPRING_CLOUD_AWS_SQS_LISTENER_MAX_RECEIVE_COUNT  = local.spring_cloud_aws.max_receive_count

    # ===== APPLICATION CONFIG =====
    SPRING_APPLICATION_NAME = var.app_name
    LOG_LEVEL               = local.current_env.log_level
  }
}