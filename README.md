# PS_Orchestrator

Microsserviço responsável por coordenar fluxos cross-service do **My Pet Admin** sem assumir ownership dos domínios de Empresa, Usuário, Contrato ou Login.

## Stack

- Java 25 LTS
- Spring Boot 4.1.1
- Spring Cloud 2025.1.3
- OpenFeign
- Spring Security
- Jakarta Validation
- Actuator
- Swagger/OpenAPI
- Docker
- GitHub Actions + JaCoCo

## Posição na arquitetura

```text
API Gateway (futuro)
  ↓
PS_Orchestrator
  ├── PS_Empresa
  ├── PS_User
  ├── PS_Contrato
  └── PS_Login
```

O Orchestrator coordena casos de uso que atravessam domínios. Cada microsserviço continua dono das próprias invariantes e dados.

O frontend permanece fora do escopo até a conclusão do Orchestrator e do API Gateway.

## Onboarding inicial

Endpoint interno:

```text
POST /internal/onboardings
X-Internal-Key: <shared-secret>
X-Onboarding-Id: <UUID>
```

O corpo contém os dados cadastrais iniciais da empresa e do titular. O titular é utilizado como nome do primeiro usuário MASTER, e o e-mail comercial informado no cadastro é utilizado como e-mail dessa identidade inicial.

Fluxo:

```text
POST /internal/onboardings
        ↓
PS_Orchestrator
        ├── PS_Empresa   POST /internal/empresas/onboarding
        ├── PS_User      POST /internal/usuarios/master
        ├── PS_Contrato  POST /contratos
        └── PS_Login     POST /internal/auth/invitations
```

Ordem oficial:

1. criar/recuperar Empresa;
2. criar/recuperar MASTER primário;
3. criar/recuperar Contrato;
4. solicitar convite de ativação ao PS_Login;
5. devolver resposta consolidada com IDs e status técnicos necessários.

O Orchestrator não recebe, persiste nem define senha.

## Idempotência ponta a ponta

`X-Onboarding-Id` é o identificador estável do caso de uso e é propagado para os domínios participantes:

- **PS_Empresa:** chave do cadastro idempotente;
- **PS_User:** `onboardingId` do MASTER inicial;
- **PS_Contrato:** `onboardingId` do contrato;
- **PS_Login:** `requestId` do convite de ativação.

Um retry com o mesmo identificador e os mesmos dados reutiliza os recursos já criados. O PS_Login não gera novo token nem reenvia o e-mail quando recebe replay da mesma `requestId`.

Reenvio legítimo de convite é um caso de uso separado e deve utilizar uma nova `requestId`; ele não exige recriar Empresa, usuário ou Contrato.

## Consistência e falhas parciais

Nesta etapa a coordenação é síncrona e não utiliza transação distribuída, Saga framework, broker ou outbox.

Se uma dependência falhar:

- recursos válidos já criados não são apagados automaticamente;
- a resposta identifica a etapa técnica que falhou sem expor payload interno ou segredo;
- um novo request com o mesmo `X-Onboarding-Id` retoma o fluxo com segurança graças à idempotência de cada serviço;
- conflitos de idempotência são propagados como `409 ONBOARDING_CONFLICT`;
- rejeições de dados de domínio são tratadas como `400 ONBOARDING_REJECTED`;
- indisponibilidade/falha inesperada de dependência é tratada como `502 ONBOARDING_DEPENDENCY_ERROR`.

O Orchestrator **não possui banco próprio** porque, no fluxo atual, os checkpoints duráveis já pertencem aos serviços responsáveis. Persistência local só deve ser introduzida se surgir necessidade concreta que não possa ser atendida pela idempotência distribuída atual.

## Segurança

- `/internal/**` exige `X-Internal-Key`;
- browser nunca recebe chave interna;
- chamadas Orchestrator → serviços propagam `X-Internal-Key`;
- `X-Correlation-Id` é gerado/propagado para rastreabilidade;
- tokens, senhas e secrets não entram nos logs;
- o futuro API Gateway será a borda externa e assumirá autenticação, roteamento e políticas transversais.

## Resposta do onboarding

A resposta consolidada não expõe dados sensíveis. Ela contém:

- `onboardingId`;
- `empresaId` e status da empresa;
- `masterUserId`;
- `contratoId`, número e status do contrato;
- status técnico da solicitação de convite.

## Observabilidade

Disponível na fundação atual:

- `GET /version`;
- `GET /actuator/health`;
- correlation ID;
- logs por `onboardingId`, etapa e IDs técnicos;
- Swagger/OpenAPI.

Evoluções previstas conforme necessidade: métricas específicas por etapa, latência por dependência e dashboards operacionais.

## Configuração

Variáveis principais:

```text
PS_EMPRESA_URL
PS_CONTRATO_URL
PS_USER_URL
PS_LOGIN_URL
INTERNAL_API_KEY
```

Porta padrão local: `8085`.

## Qualidade

O CI executa:

- Maven `clean verify`;
- JaCoCo com gate LINE >= 90% e BRANCH >= 70% para código de comportamento;
- Docker build Java 25.

O fluxo de onboarding deve ser validado também por integração cross-service com PostgreSQL real antes do merge desta entrega.

## Fora de escopo desta etapa

- frontend;
- API Gateway;
- pagamento real;
- mensageria/outbox;
- transação distribuída;
- armazenamento de senha fora do PS_Login;
- migração JWT HS256 para assinatura assimétrica/JWKS.
