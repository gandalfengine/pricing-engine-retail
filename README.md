# Pricing Engine Retail

Aplicação backend desenvolvida em **Java 21 + Spring Boot 3.x**, seguindo princípios de **Arquitetura Hexagonal**, **DDD** e boas práticas de engenharia de software.

## 🎯 Objetivo

Fornecer um endpoint REST para consulta de preços aplicáveis a um produto, considerando:

- Data de aplicação
- Identificador do produto
- Identificador da marca

### Regra principal

Quando houver mais de um preço válido para o mesmo período:

> Deve ser retornado apenas um resultado, selecionando a tarifa de maior prioridade.

---

## 🏗️ Arquitetura

O projeto segue o padrão **Hexagonal Architecture (Ports & Adapters)**:

- **domain** → regras de negócio e modelos
- **application** → casos de uso
- **infrastructure** → persistência e configurações
- **adapters**
    - `in` → entrada (REST)
    - `out` → saída (banco de dados)

---

## 🧠 Modelo de Dados

O modelo relacional foi estruturado para garantir:

- Integridade referencial
- Escalabilidade
- Clareza semântica
- Auditoria básica (controle de lifecycle)

### Diagrama ER

```mermaid
erDiagram
    BRANDS {
        BIGINT id PK "Brand identifier"
        VARCHAR name "Brand name"
        BOOLEAN active "Logical activation flag"
        TIMESTAMP created_at "Creation timestamp"
        TIMESTAMP updated_at "Last update timestamp"
    }

    PRODUCTS {
        BIGINT id PK "Product identifier"
        VARCHAR name "Product name"
        BOOLEAN active "Logical activation flag"
        TIMESTAMP created_at "Creation timestamp"
        TIMESTAMP updated_at "Last update timestamp"
    }

    CURRENCIES {
        VARCHAR iso_code PK "ISO currency code"
        VARCHAR description "Currency description"
        BOOLEAN active "Logical activation flag"
        TIMESTAMP created_at "Creation timestamp"
        TIMESTAMP updated_at "Last update timestamp"
    }

    PRICES {
        BIGINT id PK "Price record identifier"
        BIGINT brand_id FK "References BRANDS.id"
        BIGINT product_id FK "References PRODUCTS.id"
        INTEGER price_list "Applicable tariff identifier"
        INTEGER priority "Priority for overlap resolution"
        TIMESTAMP start_date "Validity start date"
        TIMESTAMP end_date "Validity end date"
        DECIMAL price "Final sale price"
        VARCHAR currency_code FK "References CURRENCIES.iso_code"
        BOOLEAN active "Logical activation flag"
        TIMESTAMP created_at "Creation timestamp"
        TIMESTAMP updated_at "Last update timestamp"
    }

    BRANDS ||--o{ PRICES : has
    PRODUCTS ||--o{ PRICES : has
    CURRENCIES ||--o{ PRICES : uses