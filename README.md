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

payload de sucesso
{
"message": "Applicable price retrieved successfully",
"correlationId": "86d7a2fd-9581-4686-ba9b-59ebb8d7183a",
"payload": {
"productId": 35455,
"brandId": 1,
"priceList": 1,
"startDate": "2020-06-14T00:00:00",
"endDate": "2020-12-31T23:59:59",
"price": 35.50
}
}

payload de erro
{
"message": "Invalid request",
"correlationId": "86d7a2fd-9581-4686-ba9b-59ebb8d7183a",
"error": {
"type": "about:blank",
"title": "Invalid request",
"status": 400,
"detail": "Invalid value for parameter: productId",
"instance": "/api/v1/prices"
}
}

coias legais.
a aplicacao possui o correlationId configurado para fazer o tracking da requisicao
o correlationID pode ser passado no header ou em caso nao exista eh criad um.


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