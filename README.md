# Challenge Softtek API

API REST em Java com Spring Boot, MongoDB e Maven, para suportar o app Android de acompanhamento de humor e avaliações de riscos psicossociais.

## 📝 Visão Geral

Funcionalidades:
- Registro e listagem de humores de colaboradores.
- Login anônimo (gera token e ID temporário).
- Avaliações de riscos psicossociais.
- Endpoint REST seguro e versionado.

## 📂 Estrutura

- `controller` → Controladores REST (`HumorController`, etc.)
- `service` → Lógica de negócio (`HumorService`)
- `repository` → Repositórios MongoDB (`HumorRepository`)
- `model` → Entidades do banco
- `dto` → Objetos de transferência de dados
- `util` → Utils como `SecurityUtils`

## ⚙️ Pré-requisitos

- Java 21
- Maven 4.x
- MongoDB rodando localmente ou remotamente

## 🏗️ Build

1. Abra o projeto no IntelliJ ou VS Code.
2. Compile e empacote usando Maven:
```bash
mvn clean package
```
3. O JAR será gerado em `target/challengeSofttekAPI-1.0-SNAPSHOT.jar`.

## 🐳 Docker

1. Crie o `Dockerfile`:
```Dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/challengeSofttekAPI-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

2. Build da imagem:
```bash
   docker build -t challenge-softtek-api .
```
3. Rodar o container:
```bash
docker run -p 8080:8080 challenge-softtek-api
```
4. Teste acessando:
```bash
http://localhost:8080/humores
```

## 🔄 Testando com [App Android](https://github.com/pedaleras/challengeSofttek)

- Certifique-se de que o backend esteja rodando (via Docker ou localmente).
- No Android Studio, rode o app no emulador.
- O Retrofit está configurado para `http://10.0.2.2:8080/`.
