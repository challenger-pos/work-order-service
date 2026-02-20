# 🚀 CI/CD Setup - Work Order Service

## Resumo: O que Você Precisa Fazer

Existem 3 tipos de configuração:
1. **GitHub Secrets** - Variáveis sensíveis
2. **GitHub Environments** - Ambientes por branch
3. **Terraform Variables** - Já configurado ✅

---

## 1️⃣ Configurar SECRETS no GitHub

### Passo 1: Acessar GitHub Secrets
```
GitHub → Settings → Secrets and variables → Actions → New repository secret
```

### Passo 2: Criar os 8 Secrets

| Secret Name | Valor | Descrição |
|-----------|-------|-----------|
| `DOCKERHUB_USERNAME` | seu_usuario_dockerhub | Username do Docker Hub |
| `DOCKERHUB_TOKEN` | seu_token_dockerhub | Token/password do Docker Hub |
| `AWS_ACCESS_KEY_ID` | sua_chave_aws | Chave AWS para CI/CD deploy |
| `AWS_SECRET_ACCESS_KEY` | sua_secret_aws | Secret AWS para CI/CD deploy |
| `DB_PASSWORD` | sua_senha_rds | Senha do PostgreSQL RDS |
| `SQS_AWS_ACCESS_KEY` | chave_para_sqs | AWS Key para SQS (app) |
| `SQS_AWS_SECRET_KEY` | secret_para_sqs | AWS Secret para SQS (app) |
| `JWT_SECRET` | sua_chave_jwt | JWT Secret para autenticação |

**Exemplo de como criar:**
```
Secret name: DOCKERHUB_USERNAME
Secret value: thiagotierre
```

---

## 2️⃣ Configurar ENVIRONMENTS no GitHub

### Passo 1: Criar 3 Environments
```
GitHub → Settings → Environments → New environment
```

### Passo 2: Configurar por Branch

**Environment 1: `dev`**
- Name: `dev`
- Deployment branches: `develop`
- (Opcional) Adicione reviewers se quiser aprovação antes do deploy

**Environment 2: `homologation`**
- Name: `homologation`
- Deployment branches: `homologation`

**Environment 3: `production`**
- Name: `production`
- Deployment branches: `main`
- ⚠️ **IMPORTANTE**: Activate "Require reviewers" para aprovação antes do deploy!

---

## 3️⃣ Verificar Branches

O CI/CD está configurado para:
- ✅ `develop` → deploy em `dev`
- ✅ `homologation` → deploy em `homologation`
- ✅ `main` → deploy em `production`

**Se não existem ainda:**
```bash
git checkout -b develop
git push -u origin develop

git checkout -b homologation
git push -u origin homologation
```

---

## 4️⃣ Processo de Deploy

### Fluxo de uma Push:

```
1. Push para develop/homologation/main
   ↓
2. CI/CD Trigger (GitHub Actions)
   ↓
3. Build & Test (Maven)
   ↓
4. Docker Build & Push para Docker Hub
   ↓
5. Terraform Init (com -backend-config correto)
   ↓
6. Terraform Plan (mostra mudanças)
   ↓
7. Terraform Apply (só se plan OK)
   ↓
8. Deploy no Kubernetes (EKS)
```

### Variáveis Automáticas:

| Variável | Origem | Uso |
|----------|--------|-----|
| `ENVIRONMENT` | Branch name | dev, homologation, production |
| `TAG` | Branch name | Docker tag (develop, homologation, main) |
| `DOCKER_IMAGE` | Secret + hardcoded | thiagotierre/work-order-service |
| `TERRAFORM_DIR` | Hardcoded | infra/ |

---

## 5️⃣ Terraform Variables Mapeadas

O CI/CD passa automaticamente para Terraform:

```hcl
TF_VAR_environment      ← ENVIRONMENT (dev/homologation/production)
TF_VAR_db_password      ← secrets.DB_PASSWORD
TF_VAR_aws_access_key   ← secrets.SQS_AWS_ACCESS_KEY
TF_VAR_aws_secret_key   ← secrets.SQS_AWS_SECRET_KEY
TF_VAR_jwt_secret       ← secrets.JWT_SECRET
TF_VAR_app_image        ← docker_image:tag
```

### Variables.tf (o que Terraform espera):

```terraform
variable "environment" { default = "dev" }
variable "db_password" { sensitive = true }
variable "aws_access_key" { sensitive = true }
variable "aws_secret_key" { sensitive = true }
variable "jwt_secret" { sensitive = true }
variable "app_image" { default = "luigigb/work-order-service:latest" }
```

✅ **Todas já existem!**

---

## 6️⃣ Terraform Backend

### Configuração Automática:

```bash
terraform init -backend-config="key=v4/work-order-service/dev/terraform.tfstate"
```

**Padrão de estados:**
```
dev        → v4/work-order-service/dev/terraform.tfstate
homologation → v4/work-order-service/homologation/terraform.tfstate
production → v4/work-order-service/production/terraform.tfstate
```

Tudo já está no S3: `tf-state-challenge-bucket`

---

## 7️⃣ Checklist Final

- [ ] Criar 8 secrets no GitHub
- [ ] Criar 3 environments (dev, homologation, production)
- [ ] Verificar branches existem (develop, homologation, main)
- [ ] Verificar Docker Hub tem acesso público
- [ ] Verificar AWS credentials têm permissão EKS + Kubernetes
- [ ] Fazer test push em develop branch
- [ ] Acompanhar GitHub Actions logs

---

## 8️⃣ Troubleshooting

**Erro: "Reference to undeclared resource"**
→ Terraform.lock.hcl está desatualizado. Deletar `.terraform/` e rerun.

**Erro: "AWS credentials invalid"**
→ Verificar `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` no GitHub Secrets.

**Erro: "Docker Hub authentication failed"**
→ Verificar `DOCKERHUB_USERNAME` e `DOCKERHUB_TOKEN` estão corretos.

**Erro: "Kubernetes cluster not found"**
→ Verificar infra/data.tf está consumindo dados remotos corretos do EKS.

---

## 📊 Monitoramento

Após configurar, monitore em:

```
GitHub → Actions → CI/CD - Spring Boot Build & Docker Hub Deploy
```

Veja logs completos de cada stage para debug.

---

## ✅ Pronto!

Após completar os passos acima, seu CI/CD estará **100% funcional** e pronto para deployar automaticamente em todos os 3 ambientes!
