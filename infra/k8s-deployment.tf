resource "kubernetes_deployment" "challengeone_app" {

  depends_on = [
    kubernetes_namespace.challengeone
  ]

  metadata {
    name      = "challengeone"
    namespace = kubernetes_namespace.challengeone.metadata[0].name
  }

  wait_for_rollout = false

  spec {
    replicas = local.current_env.replicas

    selector {
      match_labels = {
        app = "challengeone"
      }
    }

    template {
      metadata {
        labels = {
          app = "challengeone"
        }
        annotations = {
          "tags.datadoghq.com/env"          = var.environment
          "tags.datadoghq.com/service"      = var.datadog_service
          "tags.datadoghq.com/version"      = var.datadog_version
          "admission.datadoghq.com/enabled" = "true"
        }
      }

      spec {
        volume {
          name = "dd-java-agent"
          empty_dir {}
        }

        init_container {
          name    = "dd-java-agent-init"
          image   = "curlimages/curl:8.10.1"
          command = ["sh", "-c", "curl -L -o /dd/dd-java-agent.jar https://dtdg.co/latest-java-tracer"]
          volume_mount {
            name       = "dd-java-agent"
            mount_path = "/dd"
          }
        }

        container {
          name              = var.app_name
          image             = var.app_image
          image_pull_policy = "Always"

          port {
            container_port = 8080
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.app_secret.metadata[0].name
            }
          }

          env {
            name  = "SPRING_DATASOURCE_URL"
            value = "jdbc:postgresql://${data.terraform_remote_state.rds.outputs.rds_endpoint_host}:${data.terraform_remote_state.rds.outputs.rds_port}/${data.terraform_remote_state.rds.outputs.db_name}?currentSchema=${var.db_schema}"
          }

          env {
            name  = "SPRING_PROFILES_ACTIVE"
            value = var.environment
          }

          # Datadog Configuration
          env {
            name  = "JAVA_TOOL_OPTIONS"
            value = local.current_env.datadog_enabled ? "-javaagent:/dd/dd-java-agent.jar" : ""
          }
          env {
            name  = "DD_SERVICE"
            value = var.datadog_service
          }
          env {
            name  = "DD_ENV"
            value = var.environment
          }
          env {
            name  = "DD_VERSION"
            value = var.datadog_version
          }
          env {
            name  = "DD_LOGS_INJECTION"
            value = local.current_env.datadog_enabled ? "true" : "false"
          }
          env {
            name  = "DD_APPSEC_ENABLED"
            value = local.current_env.datadog_enabled ? "true" : "false"
          }
          env {
            name  = "DD_IAST_ENABLED"
            value = local.current_env.datadog_enabled ? "true" : "false"
          }
          env {
            name  = "DD_AGENT_HOST"
            value = "datadog-agent.datadog-agent.svc.cluster.local"
          }
          env {
            name  = "DD_DOGSTATSD_PORT"
            value = "8125"
          }
          # env {
          #   name  = "DATADOG_STATSD_HOST"
          #   value = "datadog-agent.datadog-agent.svc.cluster.local"
          # }
          env {
            name  = "DATADOG_STATSD_PORT"
            value = "8125"
          }
          env {
            name  = "DATADOG_STATSD_HOST"
            value = var.datadog_agent_host
          }
          env {
            name  = "DATADOG_STATSD_PORT"
            value = "8125"
          }
          env {
            name  = "DD_TRACE_DEBUG"
            value = "false"
          }
          env {
            name  = "DD_TRACE_AGENT_PORT"
            value = "8126"
          }
          env {
            name  = "DD_AGENT_PORT"
            value = "8126"
          }


          volume_mount {
            name       = "dd-java-agent"
            mount_path = "/dd"
          }

          env {
            name  = "DB_SCHEMA"
            value = var.db_schema
          }

          startup_probe {
            http_get {
              path = "/api/actuator/health/liveness"
              port = 8080
            }
            initial_delay_seconds = 180
            failure_threshold     = 30
            period_seconds        = 10
          }

          liveness_probe {
            http_get {
              path = "/api/actuator/health/liveness"
              port = 8080
            }
            initial_delay_seconds = 60
            period_seconds        = 30
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            http_get {
              path = "/api/actuator/health/readiness"
              port = 8080
            }
            initial_delay_seconds = 60
            period_seconds        = 30
          }

          resources {
            requests = {
              cpu    = var.cpu_request
              memory = var.memory_request
            }
            limits = {
              cpu    = var.cpu_limit
              memory = var.memory_limit
            }
          }

          # security_context {
          #   allow_privilege_escalation = false
          #   read_only_root_filesystem = false
          # }
        }

        # security_context {
        #   run_as_non_root = false
        # }
      }
    }

    strategy {
      type = "RollingUpdate"
      rolling_update {
        max_unavailable = 1
        max_surge       = 0
      }
    }
  }
}
