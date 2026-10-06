-- Outbox de eventos de dominio.
--
-- Existe para a gamificacao saber que um lancamento aconteceu, sem broker de
-- mensagens. A linha e gravada na MESMA transacao do lancamento: ou os dois
-- existem, ou nenhum. Nao ha janela em que a transacao foi salva e o evento se
-- perdeu -- que e exatamente o que aconteceria com uma chamada HTTP disparada
-- depois do commit.
--
-- O consumidor le por HTTP (GET /api/v1/internal/events) e guarda o proprio
-- cursor. O core nao sabe quem consome nem quantos consumidores existem.

CREATE TABLE core.outbox (
    -- Cursor do consumidor. BIGSERIAL da ordem monotonica de insercao.
    --
    -- ATENCAO ao ler isto: o id e atribuido no INSERT, mas a linha so fica
    -- visivel no COMMIT. Duas transacoes concorrentes podem receber 100 e 101 e
    -- commitar fora de ordem, deixando um "buraco" temporario -- um leitor que
    -- visse 101 e avancasse o cursor perderia 100 para sempre. Por isso o
    -- endpoint de leitura so devolve linhas com alguns segundos de idade, tempo
    -- suficiente para toda transacao concorrente ter commitado ou abortado.
    id            BIGSERIAL    PRIMARY KEY,

    event_type    VARCHAR(40)  NOT NULL,

    -- UID do Firebase; a identidade compartilhada entre os servicos.
    user_id       VARCHAR(255) NOT NULL,

    -- Id do objeto de origem (a transacao). Vira `source_id` do lado da
    -- gamificacao, onde a constraint unica garante XP concedido uma unica vez.
    aggregate_id  VARCHAR(64)  NOT NULL,

    -- Dia do lancamento, necessario para a sequencia (streak).
    --
    -- NAO existe coluna de valor aqui, de proposito: a gamificacao pontua
    -- comportamento, nunca dinheiro, e o evento nao deve carregar o que o
    -- consumidor nao pode usar.
    occurred_on   DATE         NOT NULL,

    created_at    TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Leitura do feed: "a partir do cursor, em ordem".
CREATE INDEX idx_outbox_cursor ON core.outbox (id);

-- Limpeza: eventos antigos ja consumidos podem ser descartados por rotina de
-- manutencao. O indice por data torna isso barato.
CREATE INDEX idx_outbox_created_at ON core.outbox (created_at);
