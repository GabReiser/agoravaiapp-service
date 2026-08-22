-- Schema "core" (criado pelo Flyway via quarkus.flyway.schemas=core).
-- As tabelas abaixo sao criadas no schema core (search_path).

CREATE TABLE categories (
    id     UUID         NOT NULL,
    name   VARCHAR(255) NOT NULL,
    icon   VARCHAR(255),
    color  VARCHAR(64),
    system BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE statements (
    id         UUID         NOT NULL,
    user_id    VARCHAR(255) NOT NULL,
    bank_code  VARCHAR(255),
    status     VARCHAR(50)  NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_statements PRIMARY KEY (id)
);

CREATE TABLE transactions (
    id           UUID          NOT NULL,
    user_id      VARCHAR(255)  NOT NULL,
    category_id  UUID          NOT NULL,
    description  VARCHAR(500),
    amount       NUMERIC(18,2) NOT NULL,
    type         VARCHAR(20)   NOT NULL,
    date         DATE          NOT NULL,
    source       VARCHAR(50)   NOT NULL DEFAULT 'MANUAL',
    statement_id UUID,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_transactions_statement FOREIGN KEY (statement_id) REFERENCES statements (id)
);

CREATE INDEX idx_transactions_user_date ON transactions (user_id, date);
CREATE INDEX idx_transactions_user_category ON transactions (user_id, category_id);

CREATE TABLE subscriptions (
    id          UUID          NOT NULL,
    user_id     VARCHAR(255)  NOT NULL,
    name        VARCHAR(255)  NOT NULL,
    category    VARCHAR(255),
    amount      NUMERIC(18,2) NOT NULL,
    billing_day INT           NOT NULL DEFAULT 1,
    active      BOOLEAN       NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_subscriptions PRIMARY KEY (id)
);

CREATE INDEX idx_subscriptions_user_active ON subscriptions (user_id, active);

CREATE TABLE quick_actions (
    id       UUID          NOT NULL,
    user_id  VARCHAR(255)  NOT NULL,
    label    VARCHAR(255)  NOT NULL,
    icon     VARCHAR(255),
    amount   NUMERIC(18,2),
    category VARCHAR(255),
    CONSTRAINT pk_quick_actions PRIMARY KEY (id)
);

CREATE INDEX idx_quick_actions_user ON quick_actions (user_id);
CREATE INDEX idx_statements_user ON statements (user_id);
