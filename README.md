#### Tecnologias:

+ Spring Boot com Java SDK 14
+ Docker
+ Postgres

#### Configurando o setup:

1. executando uma instância do postgres em container no docker.

> O tutorial da playlist é executado com uma base de dados <b>MySql</b>,
> no meu caso realizaei algumas alterações implementando com uma base de dados <b>postgres</b>.

A configuração necessária está contida no arquivo stack.yml na raiz do projeto.

Comando de execução:
```
$ docker-compose -f stack.yml up
```

###### Explicando alguns pontos da aplicação:

- A annotation ```@SneakyThrows```  encapsula as exception lançadas no metodo anotado e executa como ``RuntimeException```.
- Na class ```SecurityCredentialConfig``` temos o seguinte trecho de código:

```
.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

Essa configuração faz com a aplicação não guarde estado de sessão, ou seja, cada requisição é independente. Garantindo que ao realizar a implementação seremos forçados a não armazenar dados na sessão, quando estivermos utilizando diversos conteiners gerando uma "dependência" de um pod ou conteiner especifico.

- ```attempAuthentication``` 


- Um pouco mais sobre a geraç
ão dos tokens, as duas principais libs para realização desse processo são <b>nimbus-jose-jwt</b> e <b>io.jsonwebtoken</b> com mais detalhes no link: [stackoverflow-the-nimbus-jose-jwt-and-io-jsonwebtoken](https://stackoverflow.com/questions/46552922/the-nimbus-jose-jwt-and-io-jsonwebtoken-which-jjwt-library-to-pick-and-why). 
> Como mencionando no video, o <b>nimbus-jose-jwt</b> possui uma quantidade maior de funcionalidades, incluindo a parte de criptografia.