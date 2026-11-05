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
  - [OrdemServico](#ordemservico)
  - [PedidoPecas e ItemPedido](#pedidopecas-e-itempedido)
  - [Enums](#enums)
- [Rotas da API (Endpoints)](#rotas-da-api-endpoints)
  - [`/api/pecas`](#apipecas)
  - [`/api/servicos`](#apiservicos)
  - [`/api/ordens-servico`](#apiordens-servico)
  - [`/api/pedidos-pecas`](#apipedidos-pecas)
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
│   └── *Mapper.java                     # DTO <-> modelo
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

As classes de `model` são POJOs simples (atributos privados, construtor vazio, getters e setters). Não há anotações de JPA: a persistência é feita pelos repositórios JDBC. A validação com Bean Validation fica nos DTOs de requisição.

### Peca

Classe: `br.com.mecaniQA.api.model.Peca` (tabela `peca`). Representa uma peça do estoque da oficina.

| Campo | Tipo | Validação (no `PecaRequestDTO`) | Descrição |
|---|---|---|---|
| `codigoSKU` | `long` | — | Identificador da peça. Gerado pelo banco (`AUTO_INCREMENT`); qualquer valor enviado no corpo é ignorado. |
| `nome` | `String` | `@NotBlank` | Nome da peça. |
| `codigobarras` | `long` | `@Positive` | Código de barras. Deve ser maior que zero. |
| `fornecedor` | `String` | `@NotBlank` | Nome do fornecedor. |
| `quantidade` | `int` | `@PositiveOrZero` | Quantidade em estoque. |
| `precoCusto` | `double` | `@PositiveOrZero` | Preço de custo. |
| `precoVenda` | `double` | `@PositiveOrZero` | Preço de venda. |
| `tamanho` | `String` | — | Tamanho da peça. |
| `cor` | `String` | — | Cor da peça. |
| `categoriaPeca` | `CategoriaPeca` | `@NotNull` | Categoria da peça. |
| `dataCadastro` | `LocalDateTime` | — | Gerada pelo servidor ao salvar. |
| `dataAtualizacao` | `LocalDateTime` | — | Gerada pelo servidor ao salvar e a cada atualização. |

### Servico

Classe: `br.com.mecaniQA.api.model.Servico` (tabela `servico`). Representa um serviço (mão de obra) oferecido pela oficina.

| Campo | Tipo | Validação (no `ServicoRequestDTO`) | Descrição |
|---|---|---|---|
| `codigoServico` | `long` | — | Identificador. Gerado pelo banco; valor enviado no corpo é ignorado. |
| `nomeServico` | `String` | `@NotBlank` | Nome do serviço. |
| `descricaoServico` | `String` | `@NotBlank` | Descrição detalhada. |
| `tempoEstimadoMinutos` | `int` | `@Positive` | Tempo estimado, em minutos. |
| `custoTabelado` | `double` | `@PositiveOrZero` | Custo tabelado. |
| `dataCriacao` | `LocalDateTime` | — | Gerada pelo servidor. |
| `dataAtualizacao` | `LocalDateTime` | — | Gerada pelo servidor. |

### OrdemServico

Classe: `br.com.mecaniQA.api.model.OrdemServico` (tabelas `ordem_servico` e `ordem_servico_servico`). Criada pelo padrão **Builder**. Tem cliente, veículo, descrição do problema, lista de serviços, `status` (começa em `ABERTO`) e datas. O `valorTotal` é a soma do `custoTabelado` dos serviços.

### PedidoPecas e ItemPedido

Classes: `PedidoPecas` (tabela `pedido_pecas`) e `ItemPedido` (tabela `item_pedido`). `ItemPedido` é a entidade associativa entre pedido e peça e guarda a `quantidade`. O pedido começa em `ORCANDO`, e o `valorTotal` é a soma dos subtotais (preço de venda da peça x quantidade).

### Enums

- `CategoriaPeca`: `MOTOR`, `SUSPENSAO`, `FREIOS`, `ELETRICA`, `ACESSORIOS`
- `StatusOS`: `ABERTO`, `PENDENTE_PAGAMENTO`, `PAGO`, `EM_EXECUCAO`, `EXECUTADO`
- `StatusPedido`: `ORCANDO`, `PENDENTE_PAGAMENTO`, `PAGO_FATURADO`, `ENTREGUE`

---

## Rotas da API (Endpoints)

Todas as rotas recebem e devolvem JSON, sob o prefixo `/api`. Não há autenticação. Nas respostas de erro tratadas pela API (404, 400 de regra de negócio e 500), o corpo é o texto da mensagem da exceção, por exemplo `Peça com código SKU 999 não encontrada`. O 400 de campo inválido (`@Valid`) é devolvido pelo próprio Spring.

### `/api/pecas`

Controller: `PecaController`

| Método | Rota | Descrição | Sucesso | Erros possíveis |
|---|---|---|---|---|
| POST | `/api/pecas` | Cadastra uma peça (`PecaRequestDTO`). | `201` + peça criada | `400` dados inválidos |
| GET | `/api/pecas` | Lista todas as peças. | `200` + lista | `500` erro de banco |
| GET | `/api/pecas/{codigoSKU}` | Busca uma peça. | `200` | `404` não existe |
| PUT | `/api/pecas/{codigoSKU}` | Atualiza uma peça. | `200` | `400` inválido; `404` não existe |
| DELETE | `/api/pecas/{codigoSKU}` | Remove uma peça. | `204` | `404` não existe; `400` peça usada em um pedido |

Exemplo de corpo para `POST /api/pecas`:

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

### `/api/servicos`

Controller: `ServicoController`

| Método | Rota | Descrição | Sucesso | Erros possíveis |
|---|---|---|---|---|
| POST | `/api/servicos` | Cadastra um serviço (`ServicoRequestDTO`). | `201` + serviço criado | `400` dados inválidos |
| GET | `/api/servicos` | Lista todos os serviços. | `200` + lista | `500` erro de banco |
| GET | `/api/servicos/{codigoServico}` | Busca um serviço. | `200` | `404` não existe |
| PUT | `/api/servicos/{codigoServico}` | Atualiza um serviço. | `200` | `400` inválido; `404` não existe |
| DELETE | `/api/servicos/{codigoServico}` | Remove um serviço. | `204` | `404` não existe; `400` serviço usado em uma OS |

Exemplo de corpo para `POST /api/servicos`:

```json
{
  "nomeServico": "Manutenção Preventiva",
  "descricaoServico": "Revisão geral de itens de desgaste",
  "tempoEstimadoMinutos": 120,
  "custoTabelado": 150.00
}
```

### `/api/ordens-servico`

Controller: `OrdemServicoController`

| Método | Rota | Descrição | Sucesso | Erros possíveis |
|---|---|---|---|---|
| POST | `/api/ordens-servico` | Abre uma OS (`OrdemServicoRequestDTO`). | `201` + OS criada | `400` dados inválidos ou serviço informado não existe |
| GET | `/api/ordens-servico` | Lista as OS. | `200` + lista | `500` erro de banco |
| GET | `/api/ordens-servico/{codigoOS}` | Busca uma OS. | `200` | `404` não existe |
| PATCH | `/api/ordens-servico/{codigoOS}/status` | Muda o status da OS. | `200` | `404` não existe; `400` status inválido |
| DELETE | `/api/ordens-servico/{codigoOS}` | Remove uma OS. | `204` | `404` não existe |

Exemplos de corpo:

```json
{ "clienteNome": "Maria", "veiculo": "Gol 2018", "descricaoProblema": "Barulho no freio", "codigosServicos": [1, 2] }
```
```json
{ "status": "EM_EXECUCAO" }
```

### `/api/pedidos-pecas`

Controller: `PedidoPecasController`

| Método | Rota | Descrição | Sucesso | Erros possíveis |
|---|---|---|---|---|
| POST | `/api/pedidos-pecas` | Cria um pedido com itens (`PedidoPecasRequestDTO`). | `201` + pedido criado | `400` dados inválidos ou peça informada não existe |
| GET | `/api/pedidos-pecas` | Lista os pedidos. | `200` + lista | `500` erro de banco |
| GET | `/api/pedidos-pecas/{codigoPedido}` | Busca um pedido. | `200` | `404` não existe |
| POST | `/api/pedidos-pecas/{codigoPedido}/itens` | Adiciona um item ao pedido. | `200` + pedido atualizado | `404` pedido não existe; `400` peça inexistente ou dado inválido |
| PATCH | `/api/pedidos-pecas/{codigoPedido}/status` | Muda o status do pedido. | `200` | `404` não existe; `400` status inválido |
| DELETE | `/api/pedidos-pecas/{codigoPedido}` | Remove um pedido. | `204` | `404` não existe |

Exemplos de corpo:

```json
{ "itens": [ { "codigoSKU": 1, "quantidade": 2 } ] }
```
```json
{ "codigoSKU": 2, "quantidade": 1 }
```
```json
{ "status": "PENDENTE_PAGAMENTO" }
```

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

1. Crie o banco e as tabelas. No Linux/Mac/Git Bash:

```bash
mysql --default-character-set=utf8mb4 -u root -p < database/mecaniqa.sql
```

No PowerShell o `<` não funciona. Entre no cliente e use `source` (o `--default-character-set=utf8mb4` evita acentos quebrados nos dados):

```powershell
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" --default-character-set=utf8mb4 -u root -p
```
```sql
source C:/caminho/do/projeto/database/mecaniqa.sql
```

Também dá para abrir o arquivo no MySQL Workbench e executar. O script apaga e recria as tabelas.

2. Ajuste, se necessário, usuário/senha/URL com as variáveis de ambiente `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` (ou em [`application.properties`](src/main/resources/application.properties)). O padrão é `root`/`root` em `localhost:3306`. No PowerShell: `$env:DB_PASSWORD="sua_senha"` (vale só para aquele terminal).

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
