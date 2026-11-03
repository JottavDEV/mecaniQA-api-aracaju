# mecaniQA API

Repositório da API da empresa fictícia **mecaniQA**, desenvolvido para a disciplina de **Desenvolvimento Web Orientado a Objetos** na **Unex** (Aracaju).

A mecaniQA é uma oficina mecânica fictícia. A API tem como objetivo gerenciar o cadastro de **Peças** (estoque) e **Serviços** (mão de obra oferecida), servindo como base de estudo para conceitos de Spring Boot, REST e Orientação a Objetos.

## Equipe

- João Victor Carvalho de Oliveira
- Kaio Santos de Oliveira
- Marco Antonio Almeida Silva
- Nivton Amaral Alves
- Robert de Araújo Costa

---

## Sumário

- [Equipe](#equipe)
- [Stack utilizada](#stack-utilizada)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Como o sistema funciona](#como-o-sistema-funciona)
  - [Arquitetura em camadas](#arquitetura-em-camadas)
  - [Persistência de dados](#persistência-de-dados)
  - [Exceções customizadas e códigos HTTP](#exceções-customizadas-e-códigos-http)
  - [Validação](#validação)
- [Modelos de dados (Entidades)](#modelos-de-dados-entidades)
  - [Peca](#peca)
  - [Servico](#servico)
  - [Enum CategoriaPeca](#enum-categoriapeca)
- [Rotas da API (Endpoints)](#rotas-da-api-endpoints)
  - [`/api/pecas`](#apipecas)
  - [`/api/servicos`](#apiservicos)
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
| Bean Validation | `spring-boot-starter-validation` (Jakarta Validation, usado nos models e DTOs) |
| Banco de dados | **MySQL 8+**, acessado com **JDBC nativo** (`java.sql`) via `mysql-connector-j` |
| ORM | **Nenhum** — proibido nesta etapa (sem JPA, Hibernate ou Spring Data) |
| Build | Gradle (Kotlin DSL) 9.5.1, plugin `io.spring.dependency-management` |
| Testes | JUnit 5 (`spring-boot-starter-webmvc-test`, `junit-platform-launcher`) |

---

## Estrutura do projeto

```
database
└── mecaniqa.sql                         # DDL (tabelas) + DML (dados iniciais) do MySQL
src/main/java/br/com/mecaniQA/api
├── ApiApplication.java                  # Bootstrap do Spring Boot
├── config
│   └── ConexaoFactory.java              # Abre conexões JDBC (java.sql.DriverManager)
├── controller                           # Recebem a requisição, chamam o Service, decidem o status HTTP (try/catch)
├── service                              # Regras de negócio e comunicação com os repositórios
├── repository
│   ├── Repository.java                  # Interface genérica Repository<T, ID> (contrato do CRUD)
│   └── *Repository.java                 # Implementações JDBC com SQL escrito à mão
├── mapper
│   ├── ResultSetMapper.java             # Contrato dos mappers de ResultSet
│   ├── *ResultSetMapper.java            # ResultSet -> objeto de modelo
│   └── *Mapper.java                     # Modelo -> DTO de resposta
├── exception                            # Exceções customizadas
├── dto, enums, model
src/main/resources
└── application.properties               # Configuração da conexão com o MySQL
```

---

## Como o sistema funciona

### Arquitetura em camadas

```
Cliente (Postman/Insomnia/Front)
      │  JSON (DTO)
      ▼
Controller  ── só recebe a requisição, chama o Service e devolve o status HTTP
      │         (erros tratados manualmente com try/catch; sem @ExceptionHandler)
      ▼
Service     ── regras de negócio, validações e uso dos repositórios
      │         (lança as exceções customizadas)
      ▼
Repository  ── JDBC puro: PreparedStatement + SQL manual (INSERT/SELECT/UPDATE/DELETE)
      │         implementa Repository<T, ID>
      ▼
ResultSetMapper ── converte cada linha do ResultSet em objeto de modelo
      ▼
MySQL
```

Regra de ouro: **os Controllers nunca chamam os repositórios diretamente**.

### Persistência de dados

Os dados ficam no **MySQL**. O script [`database/mecaniqa.sql`](database/mecaniqa.sql) cria o banco `mecaniqa`, as tabelas (`peca`, `servico`, `ordem_servico`, `ordem_servico_servico`, `pedido_pecas`, `item_pedido`) e insere alguns dados iniciais. Ele precisa ser executado **antes** de subir a API.

Pontos do acesso a dados:

- Cada repositório abre a conexão com `ConexaoFactory` e usa `try-with-resources` (conexão, `PreparedStatement` e `ResultSet` sempre fechados).
- Todos os valores entram por `PreparedStatement` (`?`), nunca por concatenação de SQL.
- A OS (com seus serviços) e o pedido (com seus itens) são gravados em **transação** (`commit`/`rollback`).
- Tabelas associativas: `ordem_servico_servico` (OS ↔ Serviço) e `item_pedido` (Pedido ↔ Peça, com a quantidade). Apagar uma OS/pedido apaga as linhas associadas (`ON DELETE CASCADE`); apagar uma peça ou serviço ainda em uso é bloqueado (`ON DELETE RESTRICT`) e a API responde `400`.

### Exceções customizadas e códigos HTTP

Não existe `@ExceptionHandler` nem `@ControllerAdvice`: cada Controller captura as exceções com `try/catch` e define o status.

| Exceção | Quando é lançada | Status |
|---|---|---|
| `PecaNaoEncontradaException`, `ServicoNaoEncontradoException`, `OrdemServicoNaoEncontradaException`, `PedidoPecasNaoEncontradoException` (filhas de `RecursoNaoEncontradoException`) | registro não existe | `404` (ou `400` quando o item inexistente veio no corpo da requisição, ex.: serviço de uma OS) |
| `DadosInvalidosException` | dado inválido / regra violada, inclusive apagar peça ou serviço ainda em uso | `400` |
| `ErroBancoDadosException` | qualquer `SQLException` do JDBC (embrulhada pelos repositórios) | `500` |

### Validação

As anotações de Bean Validation (`@NotBlank`, `@NotNull`, `@Positive`, ...) nos models e DTOs continuam sendo disparadas pelo `@Valid` nos controllers; as validações de regra de negócio ficam nos Services.

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
| `dataCadastro` | `LocalDateTime` | — | Data/hora de cadastro da peça. **Gerada automaticamente** (`LocalDateTime.now()`) no `salvar()`; valor enviado pelo cliente é ignorado. |
| `dataAtualizacao` | `LocalDateTime` | — | Data/hora da última atualização. **Gerada automaticamente** no `salvar()` e a cada `atualizar()`; valor enviado pelo cliente é ignorado. |
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
| `dataCriacao` | `LocalDateTime` | — | Data/hora de criação do registro. **Gerada automaticamente** (`LocalDateTime.now()`) no `salvar()`; valor enviado pelo cliente é ignorado. |
| `dataAtualizacao` | `LocalDateTime` | — | Data/hora da última atualização. **Gerada automaticamente** no `salvar()` e a cada `atualizar()`; valor enviado pelo cliente é ignorado. |

Possui construtor vazio e getters/setters para todos os campos.

### Enum CategoriaPeca

Classe: [`br.com.mecaniQA.api.enums.CategoriaPeca`](src/main/java/br/com/mecaniQA/api/enums/CategoriaPeca.java)

Define as categorias possíveis para uma `Peca`:

```java
MOTOR, SUSPENSAO, FREIOS, ELETRICA, ACESSORIOS
```

---

## Rotas da API (Endpoints)

Todas as rotas recebem/retornam `JSON`. Não há autenticação nem versionamento de API configurados. Todas vivem sob o prefixo `/api`, seguindo o padrão RESTful.

### `/api/pecas`

Controller: [`PecaController`](src/main/java/br/com/mecaniQA/api/controller/PecaController.java)

| Método | Rota | Descrição | Corpo da requisição | Resposta de sucesso | Erros possíveis |
|---|---|---|---|---|---|
| `POST` | `/api/pecas` | Cadastra uma nova peça. O `codigoSKU` é gerado pelo servidor. | JSON de `Peca` (sem precisar informar `codigoSKU`) | `201 Created` + a peça criada (com `codigoSKU` gerado) | `400 Bad Request` se algum campo obrigatório for inválido |
| `GET` | `/api/pecas` | Lista todas as peças cadastradas. | — | `200 OK` + lista (`array`) de `Peca` | — |
| `GET` | `/api/pecas/{codigoSKU}` | Busca uma peça pelo código SKU. | — | `200 OK` + a peça encontrada | `404 Not Found` se o código não existir |
| `PUT` | `/api/pecas/{codigoSKU}` | Atualiza os dados de uma peça existente. | JSON de `Peca` com os novos valores | `200 OK` + a peça atualizada | `400 Bad Request` se inválido; `404 Not Found` se o código não existir |
| `DELETE` | `/api/pecas/{codigoSKU}` | Remove uma peça pelo código SKU. | — | `204 No Content` | `404 Not Found` se o código não existir |

**Exemplo de corpo para `POST /api/pecas`:**

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

> O campo `categoriaPeca` deve ser um dos valores do enum `CategoriaPeca`: `MOTOR`, `SUSPENSAO`, `FREIOS`, `ELETRICA` ou `ACESSORIOS`. Não é necessário (nem tem efeito) enviar `codigoSKU`, `dataCadastro` ou `dataAtualizacao` — todos são sempre gerados pelo servidor.

**Exemplo de erro `404 Not Found` (`GET /api/pecas/999`, código inexistente):**

```json
{
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": "Peça com código SKU 999 não encontrada",
  "timestamp": "2026-08-16T21:40:00"
}
```

**Exemplo de erro `400 Bad Request` (`POST /api/pecas` sem `nome` e com `categoriaPeca` ausente):**

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

### `/api/servicos`

Controller: [`ServicoController`](src/main/java/br/com/mecaniQA/api/controller/ServicoController.java)

| Método | Rota | Descrição | Corpo da requisição | Resposta de sucesso | Erros possíveis |
|---|---|---|---|---|---|
| `POST` | `/api/servicos` | Cadastra um novo serviço. O `codigoServico` é gerado pelo servidor. | JSON de `Servico` (sem precisar informar `codigoServico`) | `201 Created` + o serviço criado (com `codigoServico` gerado) | `400 Bad Request` se algum campo obrigatório for inválido |
| `GET` | `/api/servicos` | Lista todos os serviços cadastrados. | — | `200 OK` + lista (`array`) de `Servico` | — |
| `GET` | `/api/servicos/{codigoServico}` | Busca um serviço pelo código. | — | `200 OK` + o serviço encontrado | `404 Not Found` se o código não existir |
| `PUT` | `/api/servicos/{codigoServico}` | Atualiza os dados de um serviço existente. | JSON de `Servico` com os novos valores | `200 OK` + o serviço atualizado | `400 Bad Request` se inválido; `404 Not Found` se o código não existir |
| `DELETE` | `/api/servicos/{codigoServico}` | Remove um serviço pelo código. | — | `204 No Content` | `404 Not Found` se o código não existir |

**Exemplo de corpo para `POST /api/servicos`:**

```json
{
  "nomeServico": "Manutenção Preventiva",
  "descricaoServico": "Revisão geral de itens de desgaste",
  "tempoEstimadoMinutos": 120,
  "custoTabelado": 150.00
}
```

> Não é necessário (nem tem efeito) enviar `codigoServico`, `dataCriacao` ou `dataAtualizacao` — todos são sempre gerados pelo servidor.

---

## Detalhamento do código por classe

- **`ApiApplication`** — Classe principal (`@SpringBootApplication`).
- **`ConexaoFactory`** — Lê `db.url`, `db.username` e `db.password` do `application.properties` e devolve conexões JDBC com `DriverManager`.
- **`Repository<T, ID>`** — Interface genérica do CRUD: `save`, `findById` (`Optional`), `findAll`, `update`, `deleteById`, `existsById`.
- **`PecaRepository`, `ServicoRepository`, `OrdemServicoRepository`, `PedidoPecasRepository`** — Implementam `Repository<T, Long>` com SQL manual. Os dois últimos tratam a tabela associativa (serviços da OS / itens do pedido) em transação.
- **`*ResultSetMapper`** — Convertem a linha atual de um `ResultSet` em `Peca`, `Servico`, `OrdemServico`, `PedidoPecas` e `ItemPedido`.
- **`PecaMapper`, `ServicoMapper`, `OrdemServicoMapper`, `PedidoPecasMapper`** — Convertem DTO <-> modelo (a validação com Bean Validation fica nos DTOs de requisição).
- **`PecaService`, `ServicoService`, `OrdemServicoService`, `PedidoPecasService`** — Regras de negócio; usam os repositórios e lançam as exceções customizadas.
- **Controllers** — Chamam apenas os Services e traduzem as exceções em status HTTP com `try/catch`.
- **`OrdemServico`** — Criada pelo padrão **Builder**; o Builder também aceita código, status e datas para reconstruir uma OS lida do banco.
- **`ApiApplicationTests`** — Teste de contexto (`contextLoads`); não abre conexão, então passa mesmo sem o MySQL no ar.

---

## Como executar o projeto

Pré-requisitos: **JDK 21** (o Gradle wrapper baixa o Gradle 9.5.1 automaticamente) e **MySQL 8+** rodando.

1. Crie o banco e as tabelas:

```bash
mysql -u root -p < database/mecaniqa.sql
```

2. Ajuste, se necessário, usuário/senha/URL em [`application.properties`](src/main/resources/application.properties) (ou use as variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`). O padrão é `root`/`root` em `localhost:3306`.

3. Suba a API.

No Windows (PowerShell):

```powershell
./gradlew.bat bootRun
```

No Linux/Mac ou Git Bash:

```bash
./gradlew bootRun
```

A aplicação sobe por padrão em `http://localhost:8080`.

---

## Como testar as rotas

O script SQL já insere algumas peças e serviços iniciais; novos registros podem ser cadastrados via `POST`. Exemplos com `curl`:

```bash
# Criar uma peça (codigoSKU e datas são gerados pelo servidor, não precisam ser enviados)
curl -X POST http://localhost:8080/api/pecas \
  -H "Content-Type: application/json" \
  -d '{"nome":"Filtro de Óleo","codigobarras":123456,"fornecedor":"Mann","quantidade":20,"precoCusto":15.0,"precoVenda":30.0,"tamanho":"P","cor":"Branco","categoriaPeca":"MOTOR"}'

# Listar peças
curl http://localhost:8080/api/pecas

# Buscar uma peça inexistente -> 404 Not Found
curl -i http://localhost:8080/api/pecas/999

# Criar uma peça com dados inválidos (sem nome, sem categoria) -> 400 Bad Request
curl -i -X POST http://localhost:8080/api/pecas \
  -H "Content-Type: application/json" \
  -d '{"fornecedor":"Bosch","quantidade":10,"precoCusto":10.0,"precoVenda":20.0}'

# Criar um serviço (codigoServico e datas são gerados pelo servidor, não precisam ser enviados)
curl -X POST http://localhost:8080/api/servicos \
  -H "Content-Type: application/json" \
  -d '{"nomeServico":"Troca de Óleo","descricaoServico":"Troca de óleo e filtro","tempoEstimadoMinutos":30,"custoTabelado":80.0}'

# Listar serviços
curl http://localhost:8080/api/servicos

# Remover um serviço -> 204 No Content (ou 404 se o código não existir)
curl -i -X DELETE http://localhost:8080/api/servicos/1
```

Rodar os testes automatizados (smoke test do contexto Spring):

```bash
./gradlew test
```

---

## Limitações conhecidas / pontos de atenção

- **Sem pool de conexões**: `ConexaoFactory` abre uma conexão nova por operação (suficiente para esta etapa acadêmica).
- **Valores monetários**: ficam em `DECIMAL(10,2)` no banco, mas os models usam `double`.
- **Atualização de pedido/OS**: o `update` regrava a lista de itens/serviços (apaga e reinsere) dentro de uma transação.
