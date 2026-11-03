-- =====================================================================
-- MecâniQA - Script do banco de dados (MySQL 8+)
-- DDL (criação das tabelas) + DML (dados iniciais)
-- Execute antes de subir a API:  mysql -u root -p < database/mecaniqa.sql
-- ATENÇÃO: recria as tabelas (DROP TABLE IF EXISTS), apagando os dados.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS mecaniqa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mecaniqa;

-- ---------------------------------------------------------------------
-- DDL
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS item_pedido;
DROP TABLE IF EXISTS pedido_pecas;
DROP TABLE IF EXISTS ordem_servico_servico;
DROP TABLE IF EXISTS ordem_servico;
DROP TABLE IF EXISTS servico;
DROP TABLE IF EXISTS peca;

CREATE TABLE peca (
    codigo_sku        BIGINT         NOT NULL AUTO_INCREMENT,
    nome              VARCHAR(150)   NOT NULL,
    codigo_barras     BIGINT         NOT NULL,
    fornecedor        VARCHAR(150)   NOT NULL,
    quantidade        INT            NOT NULL DEFAULT 0,
    preco_custo       DECIMAL(10,2)  NOT NULL,
    preco_venda       DECIMAL(10,2)  NOT NULL,
    tamanho           VARCHAR(50)    NULL,
    cor               VARCHAR(50)    NULL,
    categoria         ENUM('MOTOR', 'SUSPENSAO', 'FREIOS', 'ELETRICA', 'ACESSORIOS') NOT NULL,
    data_cadastro     DATETIME       NOT NULL,
    data_atualizacao  DATETIME       NOT NULL,
    PRIMARY KEY (codigo_sku)
);

CREATE TABLE servico (
    codigo_servico          BIGINT         NOT NULL AUTO_INCREMENT,
    nome_servico            VARCHAR(150)   NOT NULL,
    descricao_servico       VARCHAR(500)   NOT NULL,
    tempo_estimado_minutos  INT            NOT NULL,
    custo_tabelado          DECIMAL(10,2)  NOT NULL,
    data_criacao            DATETIME       NOT NULL,
    data_atualizacao        DATETIME       NOT NULL,
    PRIMARY KEY (codigo_servico)
);

CREATE TABLE ordem_servico (
    codigo_os           BIGINT        NOT NULL AUTO_INCREMENT,
    cliente_nome        VARCHAR(150)  NOT NULL,
    veiculo             VARCHAR(150)  NOT NULL,
    descricao_problema  VARCHAR(500)  NOT NULL,
    status              ENUM('ABERTO', 'PENDENTE_PAGAMENTO', 'PAGO', 'EM_EXECUCAO', 'EXECUTADO')
                        NOT NULL DEFAULT 'ABERTO',
    data_abertura       DATETIME      NOT NULL,
    data_atualizacao    DATETIME      NOT NULL,
    PRIMARY KEY (codigo_os)
);

-- Tabela associativa: uma OS tem vários serviços.
-- Apagar a OS apaga estas linhas; apagar um serviço em uso é bloqueado.
CREATE TABLE ordem_servico_servico (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    codigo_os       BIGINT  NOT NULL,
    codigo_servico  BIGINT  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_oss_os      FOREIGN KEY (codigo_os)      REFERENCES ordem_servico (codigo_os)
        ON DELETE CASCADE,
    CONSTRAINT fk_oss_servico FOREIGN KEY (codigo_servico) REFERENCES servico (codigo_servico)
        ON DELETE RESTRICT
);

CREATE TABLE pedido_pecas (
    codigo_pedido     BIGINT    NOT NULL AUTO_INCREMENT,
    status            ENUM('ORCANDO', 'PENDENTE_PAGAMENTO', 'PAGO_FATURADO', 'ENTREGUE')
                      NOT NULL DEFAULT 'ORCANDO',
    data_criacao      DATETIME  NOT NULL,
    data_atualizacao  DATETIME  NOT NULL,
    PRIMARY KEY (codigo_pedido)
);

-- Entidade associativa: liga pedido e peça e guarda a quantidade.
-- Apagar o pedido apaga os itens; apagar uma peça em uso é bloqueado.
CREATE TABLE item_pedido (
    id             BIGINT  NOT NULL AUTO_INCREMENT,
    codigo_pedido  BIGINT  NOT NULL,
    codigo_sku     BIGINT  NOT NULL,
    quantidade     INT     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_pedido FOREIGN KEY (codigo_pedido) REFERENCES pedido_pecas (codigo_pedido)
        ON DELETE CASCADE,
    CONSTRAINT fk_item_peca   FOREIGN KEY (codigo_sku)    REFERENCES peca (codigo_sku)
        ON DELETE RESTRICT,
    CONSTRAINT ck_item_quantidade CHECK (quantidade > 0)
);

-- ---------------------------------------------------------------------
-- DML (dados iniciais)
-- ---------------------------------------------------------------------
INSERT INTO peca (nome, codigo_barras, fornecedor, quantidade, preco_custo, preco_venda,
                  tamanho, cor, categoria, data_cadastro, data_atualizacao) VALUES
    ('Filtro de óleo',        7891000100011, 'AutoPeças Nordeste', 40,  18.50,  34.90, NULL,    NULL,    'MOTOR',      NOW(), NOW()),
    ('Pastilha de freio',     7891000100028, 'Freios Sergipe',     25,  55.00, 109.90, 'Dianteira', NULL, 'FREIOS',     NOW(), NOW()),
    ('Amortecedor dianteiro', 7891000100035, 'Suspensão Brasil',   12, 160.00, 289.90, NULL,    'Preto', 'SUSPENSAO',  NOW(), NOW()),
    ('Bateria 60Ah',          7891000100042, 'Elétrica Aracaju',   10, 280.00, 459.90, NULL,    NULL,    'ELETRICA',   NOW(), NOW());

INSERT INTO servico (nome_servico, descricao_servico, tempo_estimado_minutos, custo_tabelado,
                     data_criacao, data_atualizacao) VALUES
    ('Troca de óleo',        'Troca do óleo do motor e do filtro',              40,  80.00, NOW(), NOW()),
    ('Alinhamento',          'Alinhamento da direção',                          60, 120.00, NOW(), NOW()),
    ('Revisão de freios',    'Inspeção e troca de pastilhas e discos',          90, 150.00, NOW(), NOW()),
    ('Diagnóstico elétrico', 'Verificação de bateria, alternador e fiação',     45,  95.00, NOW(), NOW());
