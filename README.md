# PS_Orchestrator

Microsserviço responsável por coordenar fluxos cross-service do **My Pet Admin** sem assumir ownership dos domínios de Empresa, Usuário, Contrato ou Login.

## Stack

- Java 25 LTS
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3
- OpenFeign
- Spring Security
- Actuator
- Swagger/OpenAPI
- Docker
- GitHub Actions + JaCoCo

## Responsabilidade

Fluxo alvo de onboarding:

```text
API Gateway (futuro)
  ↓
PS_Orchestrator
  ├── PS_Empresa
  ├── PS_User
  ├── PS_Contrato
  └── PS_Login
```

O Orchestrator coordena; cada microsserviço continua dono de suas próprias regras e dados.

## Contratos já mapeados

- `POST /internal/empresas` — PS_Empresa
- `POST /internal/usuarios/master` — PS_User
- `POST /contratos` — PS_Contrato
- `POST /internal/auth/invitations` — PS_Login

Chamadas internas propagam `X-Internal-Key` e `X-Correlation-Id`.

## Endpoints da fundação

- `GET /version`
- `GET /actuator/health`
- Swagger em `/swagger-ui.html`

Rotas futuras em `/internal/**` são protegidas por `X-Internal-Key`.

## Configuração

Produção exige:

- `PS_EMPRESA_URL`
- `PS_CONTRATO_URL`
- `PS_USER_URL`
- `PS_LOGIN_URL`
- `INTERNAL_API_KEY`

O serviço **não possui banco próprio nesta fundação**.

## Decisão pendente antes do onboarding real

PS_User e PS_Contrato já possuem idempotência baseada em `onboardingId`. O PS_Empresa ainda não possui um vínculo/idempotência equivalente para o primeiro passo.

Por isso a fundação não expõe ainda o endpoint de onboarding completo. Antes dele, será fechado um mecanismo de recuperação/idempotência para evitar inconsistência em falhas parciais entre a criação da Empresa e o checkpoint do Orchestrator.

## Qualidade

O CI executa:

- Maven `clean verify`;
- JaCoCo com gate LINE >= 90% e BRANCH >= 70% para código de comportamento;
- Docker build Java 25.

Frontend permanece fora do escopo nesta fase.
