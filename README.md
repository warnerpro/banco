# Projeto Faculdade - Programação Orientada a Objetos

#### Desafio: Implementar um banco de dados PostgreSQL ao back-end e criar um front-end e conectar ao back

#### Instruções de como utilizar esse projeto no VSCode

- Faz o clone do repositório
- Na pasta *banco* utiliza o comando `mvn install` para instalar as dependências do back-end
- Ainda no back-end vai em `src/resources/application.properties` e redefina a *url*, *username* e *password* de acordo com o banco de dados quee stá usando
- No back-end vai em `src/java/apresentacao/WebConfig.java` e redefina a parte de `allowedOrigin('A porta do front que está usando')`
- Na parte do front-end vai na pasta front-end e da um `yarn install` ou `npm install`
- Vai no html e executa com a extensão *live server*
- Na parte do back vai em `src/apresentacao/AcessoADado.java` e executa com *run java*
