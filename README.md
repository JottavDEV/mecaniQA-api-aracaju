# mecaniQA API

Repositório da API da empresa fictícia **mecaniQA**, desenvolvido para a disciplina de **Desenvolvimento Web Orientado a Objetos** na **Unex** (Aracaju).

A mecaniQA é uma oficina mecânica fictícia. A API tem como objetivo gerenciar o cadastro de **Peças** (estoque) e **Serviços** (mão de obra oferecida), servindo como base de estudo para conceitos de Spring Boot, REST e Orientação a Objetos.

---

## Sumário

- [Stack utilizada](#stack-utilizada)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Como o sistema funciona](#como-o-sistema-funciona)
  - [Arquitetura em camadas](#arquitetura-em-camadas)
  - [Persistência de dados (importante)](#persistência-de-dados-importante)
  - [Tratamento de erros e validação](#tratamento-de-erros-e-validação)
- [Modelos de dados (Entidades)](#modelos-de-dados-entidades)
  - [Peca](#peca)
  - [Servico](#servico)
  - [Enum Categorias](#enum-categorias)
- [Rotas da API (Endpoints)](#rotas-da-api-endpoints)
  - [`/pecas`](#pecas)
  - [`/servicos`](#servicos)
- [Detalhamento do código por classe](#detalhamento-do-código-por-classe)
- [Como executar o projeto](#como-executar-o-projeto)
- [Como testar as rotas](#como-testar-as-rotas)
- [Limitações conhecidas / pontos de atenção](#limitações-conhecidas--pontos-de-atenção)

---

## Stack utilizada

| Tecnologia | Versão / Detalhe |
|---|---|
| Java | 21 (via toolchain do Gradle) |
| Spring Boot | 4.1.0 |
| Spring Web MVC | `spring-boot-starter-webmvc` |
| Spring Data JPA | `spring-boot-starter-data-jpa` (dependência presente, porém **desabilitada** em runtime — ver seção de persistência) |
| Bean Validation | `spring-boot-starter-validation` (Jakarta Validation, usado nos models `Peca` e `Servico`) |
| Banco de dados | H2 (`com.h2database:h2`), em memória — **não está ativo atualmente** |
| Build | Gradle (Kotlin DSL) 9.5.1, plugin `io.spring.dependency-management` |
| Testes | JUnit 5 (`spring-boot-starter-webmvc-test`, `junit-platform-launcher`) |

---

## Estrutura do projeto

```
src
├── main
│   ├── java/br/com/mecaniQA/api
│   │   ├── ApiApplication.java          # Classe principal (bootstrap do Spring Boot)
│   │   ├── MeuPrimeiroApp.java          # Classe avulsa de exercício/demonstração (não faz parte da API)
│   │   ├── controller
│   │   │   ├── PecaController.java      # Endpoints REST de /pecas
│   │   │   └── ServicoController.java   # Endpoints REST de /servicos
│   │   ├── enums
│   │   │   └── Categorias.java          # Categorias possíveis de uma Peça
│   │   ├── exception
│   │   │   ├── RecursoNaoEncontradoException.java  # Exceção lançada quando um código não existe
│   │   │   └── GlobalExceptionHandler.java         # Tratamento global de erros (404 e 400)
│   │   ├── model
│   │   │   ├── Peca.java                # Entidade Peça (com validação Bean Validation)
│   │   │   └── Servico.java             # Modelo Serviço (com validação Bean Validation)
│   │   └── repository
│   │       ├── PecaRepository.java      # Armazenamento em memória das Peças (Singleton) + geração de código
│   │       └── ServicoRepository.java   # Armazenamento em memória dos Serviços (Singleton) + geração de código
│   └── resources
│       └── application.properties       # Configurações da aplicação
└── test
    └── java/br/com/mecaniQA/api
        └── ApiApplicationTests.java     # Teste de contexto (smoke test) do Spring Boot
```

Não existe camada de **Service** (regra de negócio) nem **DTO** — os `Controller`s conversam diretamente com o `Repository`, e as próprias classes de `model` são usadas como corpo de requisição/resposta. Existe, porém, uma camada transversal de **tratamento de exceções** (`exception`), usada por ambos os controllers.

---

## Como o sistema funciona

### Arquitetura em camadas

O fluxo de uma requisição HTTP é:

```
Cliente (Postman/Insomnia/Front)
      │  JSON
      ▼
Controller (@RestController)
      │  valida o corpo com @Valid (Bean Validation)
      │  chama diretamente
      ▼
Repository (Singleton, lista em memória)
      │  gera o código (SKU/Serviço) automaticamente, quando é uma criação
      ▼
Resposta em JSON (ResponseEntity com status HTTP correto: 200/201/204)
```

Se o corpo da requisição for inválido, ou se o código buscado/atualizado/removido não existir, o fluxo é desviado para o `GlobalExceptionHandler` (ver seção abaixo), que devolve uma resposta de erro padronizada em vez de propagar a exceção.

Não há um `@Service` intermediário: o `Controller` recebe o JSON já desserializado como objeto (`Peca` ou `Servico`) e repassa direto para o `Repository` correspondente, que é quem contém a lógica de CRUD.

### Persistência de dados (importante)

Apesar do projeto declarar as dependências `spring-boot-starter-data-jpa` e `h2` no `build.gradle.kts`, e da entidade `Peca` estar anotada com `@Entity`/`@Table`, o arquivo [`application.properties`](src/main/resources/application.properties) **desativa explicitamente** a auto-configuração de banco de dados:

```properties
spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
```

Ou seja, **nenhum banco de dados real é utilizado no momento**. Os dados de Peças e Serviços ficam guardados apenas em **listas em memória** (`ArrayList`), dentro das classes `PecaRepository` e `ServicoRepository`, que são implementadas como **Singleton** (uma única instância viva durante a execução da aplicação).

Consequência prática: **todos os dados cadastrados são perdidos quando a aplicação é reiniciada** — e, junto com eles, o contador usado para gerar os códigos também volta a zero (ver seção seguinte).

### Tratamento de erros e validação

A API centraliza o tratamento de erros em [`GlobalExceptionHandler`](src/main/java/br/com/mecaniQA/api/exception/GlobalExceptionHandler.java), uma classe `@RestControllerAdvice` que intercepta exceções lançadas pelos controllers e as converte em respostas HTTP padronizadas:

| Situação | Exceção | Status HTTP | Corpo da resposta |
|---|---|---|---|
| Código (`codigoSKU`/`codigoServico`) não encontrado em `GET`/`PUT`/`DELETE` por id | `RecursoNaoEncontradoException` | `404 Not Found` | `{ "status": 404, "erro": "Recurso não encontrado", "mensagem": "...", "timestamp": "..." }` |
| Corpo de `POST`/`PUT` com campos inválidos (Bean Validation) | `MethodArgumentNotValidException` | `400 Bad Request` | `{ "status": 400, "erro": "Dados inválidos", "campos": { "nome": "O nome da peça é obrigatório", ... }, "timestamp": "..." }` |

As regras de validação ficam declaradas diretamente nos atributos de `Peca` e `Servico` (anotações `jakarta.validation.constraints`, como `@NotBlank`, `@NotNull`, `@Positive` e `@PositiveOrZero` — detalhadas na tabela de cada model, mais abaixo) e são disparadas automaticamente pelo `@Valid` presente nos parâmetros `@RequestBody` dos métodos de criação e atualização dos controllers.

---

## Modelos de dados (Entidades)

### Peca

Classe: [`br.com.mecaniQA.api.model.Peca`](src/main/java/br/com/mecaniQA/api/model/Peca.java)

Representa uma peça do estoque da oficina. Está anotada com `@Entity` e `@Table(name = "pecas")`, mas — como explicado acima — essa anotação não tem efeito prático hoje, pois o JPA está desligado. Também não possui nenhum campo marcado com `@Id`, então, caso o JPA fosse reativado como está, o mapeamento falharia por falta de chave primária.

| Campo | Tipo | Validação | Descrição |
|---|---|---|---|
| `codigoSKU` | `long` | — | Código identificador da peça (usado como "id" nas rotas). **Gerado automaticamente pelo `PecaRepository`** a cada `POST`; qualquer valor enviado pelo cliente no corpo da requisição é ignorado e sobrescrito. |
| `nome` | `String` | `@NotBlank` | Nome da peça. Obrigatório. |
| `codigobarras` | `long` | `@Positive` | Código de barras da peça. Deve ser maior que zero. |
| `fornecedor` | `String` | `@NotBlank` | Nome do fornecedor. Obrigatório. |
| `quantidade` | `int` | `@PositiveOrZero` | Quantidade em estoque. Não pode ser negativa. |
| `precoCusto` | `double` | `@PositiveOrZero` | Preço de custo (compra). Não pode ser negativo. |
| `precoVenda` | `double` | `@PositiveOrZero` | Preço de venda. Não pode ser negativo. |
| `dataCadastro` | `LocalDateTime` | — | Data/hora de cadastro da peça. |
| `dataAtualizacao` | `LocalDateTime` | — | Data/hora da última atualização. |
| `tamanho` | `String` | — | Tamanho da peça. |
| `cor` | `String` | — | Cor da peça. |
| `categoriaPeca` | `CategoriaPeca` (enum) | `@NotNull` | Categoria da peça. Obrigatória; persistida/serializada como texto (`EnumType.STRING`). |

Possui construtor vazio e getters/setters para todos os campos.

### Servico

Classe: [`br.com.mecaniQA.api.model.Servico`](src/main/java/br/com/mecaniQA/api/model/Servico.java)

Representa um serviço/mão de obra oferecido pela oficina (ex.: manutenção preventiva). Diferente de `Peca`, **não é uma entidade JPA** (não possui `@Entity`) — é um POJO simples.

| Campo | Tipo | Validação | Descrição |
|---|---|---|---|
| `codigoServico` | `long` | — | Código identificador do serviço (usado como "id" nas rotas). **Gerado automaticamente pelo `ServicoRepository`** a cada `POST`; qualquer valor enviado pelo cliente no corpo da requisição é ignorado e sobrescrito. |
| `nomeServico` | `String` | `@NotBlank` | Nome do serviço (ex.: "Manutenção Preventiva"). Obrigatório. |
| `descricaoServico` | `String` | `@NotBlank` | Descrição detalhada do serviço. Obrigatória. |
| `tempoEstimadoMinutos` | `int` | `@Positive` | Tempo estimado de execução, em minutos. Deve ser maior que zero. |
| `custoTabelado` | `double` | `@PositiveOrZero` | Custo tabelado do serviço. Não pode ser negativo. |
| `dataCriacao` | `LocalDateTime` | — | Data/hora de criação do registro. |
| `dataAtualizacao` | `LocalDateTime` | — | Data/hora da última atualização. |

Possui construtor vazio e getters/setters para todos os campos.

### Enum Categorias

Classe: [`br.com.mecaniQA.api.enums.Categorias`](src/main/java/br/com/mecaniQA/api/enums/Categorias.java)

Define as categorias possíveis para uma `Peca`:

```java
MOTOR, SUSPENSAO, FREIOS, ELETRICA, ACESSORIOS
```

---

## Rotas da API (Endpoints)

Todas as rotas recebem/retornam `JSON`. Não há autenticação nem versionamento de API configurados.

### `/pecas`

Controller: [`PecaController`](src/main/java/br/com/mecaniQA/api/controller/PecaController.java)

| Método | Rota | Descrição | Corpo da requisição | Resposta de sucesso | Erros possíveis |
|---|---|---|---|---|---|
| `POST` | `/pecas` | Cadastra uma nova peça. O `codigoSKU` é gerado pelo servidor. | JSON de `Peca` (sem precisar informar `codigoSKU`) | `201 Created` + a peça criada (com `codigoSKU` gerado) | `400 Bad Request` se algum campo obrigatório for inválido |
| `GET` | `/pecas` | Lista todas as peças cadastradas. | — | `200 OK` + lista (`array`) de `Peca` | — |
| `GET` | `/pecas/{codigoSKU}` | Busca uma peça pelo código SKU. | — | `200 OK` + a peça encontrada | `404 Not Found` se o código não existir |
| `PUT` | `/pecas/{codigoSKU}` | Atualiza os dados de uma peça existente. | JSON de `Peca` com os novos valores | `200 OK` + a peça atualizada | `400 Bad Request` se inválido; `404 Not Found` se o código não existir |
| `DELETE` | `/pecas/{codigoSKU}` | Remove uma peça pelo código SKU. | — | `204 No Content` | `404 Not Found` se o código não existir |

**Exemplo de corpo para `POST /pecas`:**

```json
{
  "nome": "Pastilha de Freio",
  "codigobarras": 7891234567890,
  "fornecedor": "Bosch",
  "quantidade": 50,
  "precoCusto": 45.90,
  "precoVenda": 89.90,
  "tamanho": "Padrão",
  "cor": "Preta",
  "categoriaPeca": "FREIOS"
}
```

> O campo `categoriaPeca` deve ser um dos valores do enum `CategoriaPeca`: `MOTOR`, `SUSPENSAO`, `FREIOS`, `ELETRICA` ou `ACESSORIOS`. Não é necessário (nem tem efeito) enviar `codigoSKU` — ele é sempre gerado pelo servidor.

**Exemplo de erro `404 Not Found` (`GET /pecas/999`, código inexistente):**

```json
{
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": "Peça com código SKU 999 não encontrada",
  "timestamp": "2026-08-16T21:40:00"
}
```

**Exemplo de erro `400 Bad Request` (`POST /pecas` sem `nome` e com `categoriaPeca` ausente):**

```json
{
  "status": 400,
  "erro": "Dados inválidos",
  "campos": {
    "nome": "O nome da peça é obrigatório",
    "categoriaPeca": "A categoria da peça é obrigatória"
  },
  "timestamp": "2026-08-16T21:40:00"
}
```

### `/servicos`

Controller: [`ServicoController`](src/main/java/br/com/mecaniQA/api/controller/ServicoController.java)

| Método | Rota | Descrição | Corpo da requisição | Resposta de sucesso | Erros possíveis |
|---|---|---|---|---|---|
| `POST` | `/servicos` | Cadastra um novo serviço. O `codigoServico` é gerado pelo servidor. | JSON de `Servico` (sem precisar informar `codigoServico`) | `201 Created` + o serviço criado (com `codigoServico` gerado) | `400 Bad Request` se algum campo obrigatório for inválido |
| `GET` | `/servicos` | Lista todos os serviços cadastrados. | — | `200 OK` + lista (`array`) de `Servico` | — |
| `GET` | `/servicos/{codigoServico}` | Busca um serviço pelo código. | — | `200 OK` + o serviço encontrado | `404 Not Found` se o código não existir |
| `PUT` | `/servicos/{codigoServico}` | Atualiza os dados de um serviço existente. | JSON de `Servico` com os novos valores | `200 OK` + o serviço atualizado | `400 Bad Request` se inválido; `404 Not Found` se o código não existir |
| `DELETE` | `/servicos/{codigoServico}` | Remove um serviço pelo código. | — | `204 No Content` | `404 Not Found` se o código não existir |

**Exemplo de corpo para `POST /servicos`:**

```json
{
  "nomeServico": "Manutenção Preventiva",
  "descricaoServico": "Revisão geral de itens de desgaste",
  "tempoEstimadoMinutos": 120,
  "custoTabelado": 150.00
}
```

> Não é necessário (nem tem efeito) enviar `codigoServico` — ele é sempre gerado pelo servidor.

---

## Detalhamento do código por classe

- **`ApiApplication`** — Classe principal, anotada com `@SpringBootApplication`. Ponto de entrada da aplicação (`main`), que sobe o servidor embarcado do Spring Boot.

- **`MeuPrimeiroApp`** — Classe isolada com seu próprio `main`, criada como exercício/demonstração inicial da disciplina. Instancia um `Servico`, define o nome e imprime no console. **Não faz parte do fluxo da API** (não é um `@Component`/`@RestController` e não é executada pelo Spring Boot).

- **`PecaController`** — Expõe o CRUD de peças em `/pecas`. Obtém a instância única de `PecaRepository` via `getInstance()` e delega cada operação para o repositório. Valida o corpo da requisição com `@Valid` em `POST`/`PUT`; quando `buscarPorId`, `atualizar` ou `deletar` não encontram o código informado, lança `RecursoNaoEncontradoException`. Retorna `ResponseEntity` com o status HTTP apropriado (`201` na criação, `200` em busca/atualização, `204` na remoção).

- **`ServicoController`** — Expõe o CRUD de serviços em `/servicos`. Mesmo padrão do `PecaController`: obtém `ServicoRepository.getInstance()` no construtor, valida com `@Valid`, lança `RecursoNaoEncontradoException` quando o código não existe e devolve `ResponseEntity` com o status correto.

- **`PecaRepository`** — Implementa o padrão **Singleton** (construtor privado + `getInstance()` estático) e mantém uma `List<Peca>` em memória, além de um contador `AtomicLong contadorId` usado para gerar códigos. Métodos:
  - `salvar(Peca)`: **sobrescreve `codigoSKU` com o próximo valor do contador** (`contadorId.incrementAndGet()`), adiciona a peça à lista e a retorna.
  - `listarTodas()`: retorna a lista completa.
  - `buscarPorId(long)`: percorre a lista procurando `codigoSKU` igual; retorna `null` se não achar (o `null` é tratado pelo controller, que lança a exceção de 404).
  - `atualizar(long, Peca)`: busca a peça existente e sobrescreve todos os campos (exceto `codigoSKU`, que nunca é alterado) com os valores recebidos; retorna `null` se o código não existir.
  - `deletar(long)`: busca e remove a peça da lista; retorna `boolean` indicando sucesso.

- **`ServicoRepository`** — Mesmo padrão **Singleton** aplicado a `List<Servico>`, com seu próprio `AtomicLong contadorId`. Métodos equivalentes: `salvar` (gera `codigoServico` automaticamente), `getServicos` (lista todos), `buscarPorID`, `atualizar` (sobrescreve `nomeServico`, `custoTabelado`, `dataAtualizacao`, `descricaoServico` e `tempoEstimadoMinutos`), `deletar`.

- **`RecursoNaoEncontradoException`** — `RuntimeException` simples, lançada pelos controllers quando um `codigoSKU`/`codigoServico` não é encontrado no repositório.

- **`GlobalExceptionHandler`** — Classe `@RestControllerAdvice` que centraliza o tratamento de exceções da API. Possui dois `@ExceptionHandler`: um para `RecursoNaoEncontradoException` (devolve `404 Not Found`) e outro para `MethodArgumentNotValidException` (devolve `400 Bad Request` com o mapa de campos inválidos e suas mensagens, extraído de `ex.getBindingResult().getFieldErrors()`).

- **`CategoriaPeca`** — Enum simples com as categorias de peças suportadas.

- **`Peca`** / **`Servico`** — Classes de modelo (POJOs) com atributos privados, construtor vazio, getters/setters e anotações de Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`, `@PositiveOrZero`) nos campos obrigatórios. São usadas tanto para representar os dados internamente quanto como corpo de requisição/resposta JSON nos controllers (não há DTOs separados).

- **`ApiApplicationTests`** — Teste de contexto padrão gerado pelo Spring Boot (`@SpringBootTest`), apenas garante que o contexto da aplicação sobe sem erros (`contextLoads`).

---

## Como executar o projeto

Pré-requisitos: **JDK 21** (o Gradle wrapper baixa o Gradle 9.5.1 automaticamente).

No Windows (PowerShell):

```powershell
./gradlew.bat bootRun
```

No Linux/Mac ou Git Bash:

```bash
./gradlew bootRun
```

A aplicação sobe por padrão em `http://localhost:8080` (porta padrão do Spring Boot, não customizada em `application.properties`).

---

## Como testar as rotas

Como não há dados pré-carregados nem banco persistente, é necessário cadastrar peças/serviços via `POST` antes de consultá-los. Exemplos com `curl`:

```bash
# Criar uma peça (codigoSKU é gerado pelo servidor, não precisa ser enviado)
curl -X POST http://localhost:8080/pecas \
  -H "Content-Type: application/json" \
  -d '{"nome":"Filtro de Óleo","codigobarras":123456,"fornecedor":"Mann","quantidade":20,"precoCusto":15.0,"precoVenda":30.0,"tamanho":"P","cor":"Branco","categoriaPeca":"MOTOR"}'

# Listar peças
curl http://localhost:8080/pecas

# Buscar uma peça inexistente -> 404 Not Found
curl -i http://localhost:8080/pecas/999

# Criar uma peça com dados inválidos (sem nome, sem categoria) -> 400 Bad Request
curl -i -X POST http://localhost:8080/pecas \
  -H "Content-Type: application/json" \
  -d '{"fornecedor":"Bosch","quantidade":10,"precoCusto":10.0,"precoVenda":20.0}'

# Criar um serviço (codigoServico é gerado pelo servidor, não precisa ser enviado)
curl -X POST http://localhost:8080/servicos \
  -H "Content-Type: application/json" \
  -d '{"nomeServico":"Troca de Óleo","descricaoServico":"Troca de óleo e filtro","tempoEstimadoMinutos":30,"custoTabelado":80.0}'

# Listar serviços
curl http://localhost:8080/servicos

# Remover um serviço -> 204 No Content (ou 404 se o código não existir)
curl -i -X DELETE http://localhost:8080/servicos/1
```

Rodar os testes automatizados (smoke test do contexto Spring):

```bash
./gradlew test
```

---

## Limitações conhecidas / pontos de atenção

Estes pontos refletem o estado atual do código e são úteis para quem for evoluir o projeto:

- **Sem persistência real**: os dados existem apenas em memória (`ArrayList` dentro dos repositórios Singleton) e são perdidos a cada reinicialização da aplicação, mesmo com H2 e Spring Data JPA nas dependências. Os contadores de geração de código (`AtomicLong`) também são reiniciados junto.
- **Sem camada de serviço (`@Service`)**: a regra de negócio (hoje mínima) está toda dentro dos repositórios; os controllers chamam os repositórios diretamente.
- **`Peca` está anotada como `@Entity` mas não é gerenciada pelo JPA** (autoconfiguração desativada) **e não possui `@Id`**, o que impediria o mapeamento caso o JPA fosse reativado sem ajustes.
- **`MeuPrimeiroApp.java`** é uma classe de exercício isolada, sem relação com os endpoints da API — mantê-la ou removê-la não afeta o funcionamento do serviço REST.
- **Corpo de erro sem `@ControllerAdvice` para exceções genéricas**: exceções não mapeadas (ex.: erro de parsing de JSON malformado, `NumberFormatException` num `@PathVariable`) ainda caem no tratamento padrão do Spring, não no formato padronizado do `GlobalExceptionHandler`.
