4# Bikes API: automacao, observabilidade e DevSecOps

API Java com Spring Boot. O foco do teste funcional descrito aqui e a atualizacao
de senha de usuario; o projeto tambem sobe uma infraestrutura de banco,
monitoramento e centralizacao de logs para a materia de infraestrutura.

## Stack

- Java 21 + Spring Boot
- MySQL para persistencia
- Prometheus + Actuator + Micrometer
- Grafana com dashboard provisionado
- Graylog + MongoDB + OpenSearch
- GitHub Actions publicando no GHCR

## Subir o sistema

Pre-requisitos: Docker Engine com Docker Compose v2 e aproximadamente 4 GB de
memoria disponivel. Na raiz do projeto, execute:

```bash
docker compose up --build
```

O primeiro build pode levar alguns minutos. Aguarde a inicializacao dos
containers e confirme que a API esta respondendo em
http://localhost:8080/actuator/health antes de enviar requisicoes.

## Testar atualizacao de senha (Spring Boot)

As requisicoes podem ser feitas pelo Postman ou pelo Swagger UI em
http://localhost:8080/docs-bikes.html. No Postman, use o header
`Content-Type: application/json` nos requests que enviam body.

### 1. Criar um usuario para o teste

Envie esta requisicao para criar um usuario. A senha precisa ter exatamente
6 caracteres. Se ja existir um usuario com esse e-mail, escolha outro.

```http
POST http://localhost:8080/api/v1/usuarios
Content-Type: application/json
```

```json
{
  "username": "teste@example.com",
  "password": "abc123"
}
```

A resposta esperada e `201 Created`. Anote o `id` retornado; ele sera usado
nas proximas requisicoes.

### 2. Atualizar a senha

Substitua `{id}` pelo identificador recebido ao criar o usuario.

```http
PATCH http://localhost:8080/api/v1/usuarios/{id}
Content-Type: application/json
```

```json
{
  "senhaAtual": "abc123",
  "novaSenha": "def456",
  "confirmarSenha": "def456"
}
```

Se a senha atual estiver correta e a nova senha for igual a confirmacao, a API
retorna `204 No Content`. Uma resposta sem body e o comportamento esperado para
esse status.

### 3. Confirmar a alteracao

Tente atualizar novamente usando a senha antiga como `senhaAtual`. A API deve
retornar `400 Bad Request`, indicando que a senha atual nao confere. Em seguida,
envie outro `PATCH` com `"senhaAtual": "def456"` para confirmar que a nova senha
foi aceita. Esse ultimo request tambem atualiza a senha; escolha uma nova senha
de 6 caracteres para `novaSenha` e `confirmarSenha`.

Outros resultados que podem ser demonstrados:

- Senhas novas diferentes entre si: `400 Bad Request`.
- ID de usuario inexistente: `404 Not Found`.
- Campo ausente ou senha com tamanho diferente de 6: `422 Unprocessable Content`.

O recurso `GET /api/v1/usuarios/{id}` retorna o usuario, mas nao expoe a senha;
por isso a verificacao e feita tentando usar a senha antiga e depois a nova
como senha atual.

## Servicos de infraestrutura

O Compose inicia a API e seus servicos de suporte:

| Servico | URL | Credenciais |
| --- | --- | --- |
| API | http://localhost:8080 | - |
| Swagger | http://localhost:8080/docs-bikes.html | - |
| Actuator | http://localhost:8080/actuator/health | - |
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin / admin |
| Graylog | http://localhost:9000 | admin / admin |

O MySQL e usado pela API para persistencia. O servico `graylog-init` cria
automaticamente a entrada GELF UDP na porta `12201`; a API envia logs
estruturados para o Graylog e tambem os imprime no console do container.

Para acompanhar o estado dos containers, use outro terminal na raiz do projeto:

```bash
docker compose ps
```

Para parar os containers sem apagar os dados persistidos:

```bash
docker compose down
```

`docker compose down -v` tambem remove os volumes, inclusive os dados do MySQL,
Grafana, Prometheus, MongoDB e OpenSearch. Use essa opcao somente se quiser
apagar os dados locais.

## Pipeline CI/CD

O workflow em `.github/workflows/ci-cd.yml` roda em push e pull request para `main` ou `master`. Ele executa `./mvnw clean verify`, constroi a imagem pelo `Dockerfile` multistage e, em push, publica tags com SHA e `latest` no GitHub Container Registry.

A publicacao usa `GITHUB_TOKEN`; habilite `read and write permissions` para Actions em Settings > Actions > General.

## Evidencias para entrega

1. Pipeline verde na aba Actions do GitHub.
2. Dashboard `Bikes API - Spring Boot` em `http://localhost:3000/d/bikes-api` com chamadas para `GET /api/v1/usuarios`.
3. Graylog com a busca `application:bikes` ou `facility:bikes` exibindo eventos GELF.
4. Prometheus em `http://localhost:9090/targets` mostrando o target `bikes` como `UP`.

## Decisoes de seguranca e operacao

- O `Dockerfile` usa dois estagios: Maven/JDK para build e JRE Alpine para runtime.
- O processo da API roda como usuario sem privilegios.
- Healthchecks controlam a ordem de inicializacao de MySQL, Graylog, API e Prometheus.
- As credenciais sao demonstrativas; em ambiente real devem vir de secrets.
