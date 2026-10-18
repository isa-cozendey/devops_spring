# Projeto da Disciplina de Devops
## Bikes API: automação, observabilidade e DevSecOps

## Stack

- Java 21 + Spring Boot
- MySQL 
- Prometheus + Actuator + Micrometer
- Grafana com dashboard 
- Graylog + MongoDB + OpenSearch
- GitHub Actions publicando no GHCR

## Subir o sistema

Pré-requisitos: Docker Engine com Docker Compose v2 e aproximadamente 4 GB de
memoria disponível. Na raiz do projeto, execute:

```bash
docker compose up --build
```

O primeiro build pode levar alguns minutos. Aguarde a inicialização dos
containers e confirme que a API está respondendo
http://localhost:8080/actuator/health antes de enviar requisições.

<img width="403" height="465" alt="image" src="https://github.com/user-attachments/assets/12e26b02-d9f5-474b-8d48-613637585dda" />


## Infraestrutura

O Compose inicia a API e seus serviços de suporte:

| Servico | URL | Credenciais |
| --- | --- | --- |
| API | http://localhost:8080 | - |
| Swagger | http://localhost:8080/docs-bikes.html | - |
| Actuator | http://localhost:8080/actuator/health | - |
| Prometheus | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin / admin |
| Graylog | http://localhost:9000 | admin / admin |

O MySQL é usado pela API para persistência. O servico `graylog-init` cria
automaticamente a entrada GELF UDP na porta `12201`; a API envia logs
estruturados para o Graylog e também os imprime no console do container.

Para acompanhar o estado dos containers, em outro terminal na raiz do projeto:

```bash
docker compose ps
```

Para parar os containers sem apagar os dados persistidos:

```bash
docker compose down
```

`docker compose down -v` tambem remove os volumes, inclusive os dados do MySQL,
Grafana, Prometheus, MongoDB e OpenSearch. Pode ser usado para apagar os dados locais.

## Pipeline CI/CD

O workflow em `.github/workflows/ci-cd.yml` roda em push e pull request para `main` ou `master`. Ele executa `./mvnw clean verify`, constroi a imagem pelo `Dockerfile` multistage e, em push, publica tags com SHA e `latest` no GitHub Container Registry. A publicacao usa `GITHUB_TOKEN`.

## Entrega

1. **Docker:** cria e executa os containers. O Compose define quais serviços existem, como se conectam, quais portas usam e quais dependências precisam estar disponíveis antes de outros serviços iniciarem.

   <img width="1222" height="245" alt="image" src="https://github.com/user-attachments/assets/b82db33f-1ae1-463f-ad9e-c77ed1119fa6" />
   
2. **Pipeline:** na aba GitHub Actions, automatiza a entrega. Pois quando são enviadas alterações para a branch principal, pipeline executa os testes, constrói a imagem Docker, em caso de push, publica a imagem no GitHub Container Registry (GHCR).

   <img width="1897" height="885" alt="image" src="https://github.com/user-attachments/assets/440bf56b-ed15-446c-a39c-382157120b92" />
   
3. **Prometheus:** coleta e armazena métricas, Realiza consultas periódicas ao endpoint da aplicação, coleta e armazena valores numéricos. Por exemplo: memória utilizada, CPU, número de requisições e a duração delas.
   
   <img width="1916" height="365" alt="image" src="https://github.com/user-attachments/assets/88f8fe9c-1f9d-49c9-b9b8-73681585d44e" />
   <img width="1900" height="901" alt="image" src="https://github.com/user-attachments/assets/ac1c49ce-dd67-4748-ac71-ca49b8d7a26d" />

4. **Grafana:** consulta o Prometheus e utiliza métricas para gerar gráficos. O dashboard é configurado para exibir as informações, como CPU, memória heap, requisições por segundo, códigos de status HTTP, tempo de resposta, entre outros.
   
   <img width="1897" height="910" alt="image" src="https://github.com/user-attachments/assets/5831b9af-58a5-4248-8bf9-7da7dacaa7ff" />

5. **Graylog:** centraliza logs e permite pesquisas. Recebe mensagens enviadas pela aplicação, organiza os registros e permite o monitoramento de erros, por exemplo.

    <img width="1917" height="546" alt="image" src="https://github.com/user-attachments/assets/de347af9-dd2d-4b75-a4de-629221b4dccd" />
    <img width="897" height="717" alt="image" src="https://github.com/user-attachments/assets/af2cc43b-d998-49ec-b54b-8222ba36cb60" />

    Exemplos de consulta:
   ```Consulta Graylog
     "Started BikesApplication"
     "Duplicate entry"
     source:bikes AND 1062
   ```

## Decisões de segurança e operação

- O `Dockerfile` usa dois estagios: Maven/JDK para build e JRE Alpine para runtime.
- O processo da API roda como usuario sem privilegios.
- Healthchecks controlam a ordem de inicializacao de MySQL, Graylog, API e Prometheus.
- As credenciais sao demonstrativas; em ambiente real devem vir de secrets.
