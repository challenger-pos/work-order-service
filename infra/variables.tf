# ========================
# PROJETO & AMBIENTE
# ========================
variable "project_name" {
  description = "Nome do projeto"
  type        = string
  default     = "challengeone-g19"
}

variable "environment" {
  description = "Ambiente (dev, homologation, production)"
  type        = string
  default     = "dev"
  validation {
    condition     = contains(["dev", "homologation", "production"], var.environment)
    error_message = "Environment deve ser dev, homologation ou production"
  }
}

variable "tags" {
  description = "Tags para recursos AWS"
  type        = map(string)
  default = {
    Name        = "g19-challengeone"
    ManagedBy   = "Terraform"
    Repository  = "work-order-service"
  }
}

# ========================
# APLICAÇÃO
# ========================
variable "app_name" {
  description = "Nome da aplicação"
  type        = string
  default     = "work-order-service"
}

variable "app_image" {
  description = "Docker image da aplicação"
  type        = string
  default     = "luigigb/work-order-service:latest"
}

# ========================
# BANCO DE DADOS
# ========================
variable "db_schema" {
  description = "Schema do banco de dados"
  type        = string
  default     = "public"
}

variable "db_password" {
  description = "Senha do banco de dados (SENSÍVEL - não usar em tfvars)"
  type        = string
  sensitive   = true
}

# ========================
# AWS CREDENTIALS (SENSÍVEIS)
# ========================
variable "aws_access_key" {
  description = "AWS Access Key ID para SQS (SENSÍVEL - não usar em tfvars)"
  type        = string
  sensitive   = true
}

variable "aws_secret_key" {
  description = "AWS Secret Access Key para SQS (SENSÍVEL - não usar em tfvars)"
  type        = string
  sensitive   = true
}

# ========================
# JWT & SEGURANÇA (SENSÍVEL)
# ========================
variable "jwt_secret" {
  description = "JWT secret key para autenticação (SENSÍVEL - não usar em tfvars)"
  type        = string
  sensitive   = true
}

# ========================
# RECURSOS KUBERNETES (DEFAULTS)
# ========================
variable "cpu_request" {
  description = "CPU request para pod"
  type        = string
  default     = "50m"
}

variable "cpu_limit" {
  description = "CPU limit para pod"
  type        = string
  default     = "500m"
}

variable "memory_request" {
  description = "Memory request para pod"
  type        = string
  default     = "512Mi"
}

variable "memory_limit" {
  description = "Memory limit para pod"
  type        = string
  default     = "1Gi"
}

# ========================
# DATADOG (DEFAULTS)
# ========================
variable "datadog_enabled" {
  description = "Ativar Datadog tracing"
  type        = bool
  default     = true
}

variable "datadog_service" {
  description = "Datadog service name"
  type        = string
  default     = "work-order-service"
}

variable "datadog_version" {
  description = "Versão da aplicação para Datadog"
  type        = string
  default     = "1.0.0"
}