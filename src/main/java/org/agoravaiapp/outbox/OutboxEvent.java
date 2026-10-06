package org.agoravaiapp.outbox;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Evento de dominio publicado para consumidores externos (hoje, a gamificacao).
 *
 * <p>Gravado na mesma transacao do fato que o originou. Ver o cabecalho de
 * {@code V3__create_outbox.sql} para o motivo e para a armadilha do cursor.</p>
 */
@Entity
@Table(name = "outbox", schema = "core")
public class OutboxEvent extends PanacheEntityBase {

    /** Lancamento criado pelo usuario. */
    public static final String TRANSACTION_CREATED = "TRANSACTION_CREATED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "event_type", nullable = false, length = 40)
    public String eventType;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(name = "aggregate_id", nullable = false, length = 64)
    public String aggregateId;

    @Column(name = "occurred_on", nullable = false)
    public LocalDate occurredOn;

    @Column(name = "created_at", nullable = false, updatable = false)
    public Instant createdAt = Instant.now();

    public static OutboxEvent transactionCreated(String userId, String transactionId, LocalDate date) {
        OutboxEvent event = new OutboxEvent();
        event.eventType = TRANSACTION_CREATED;
        event.userId = userId;
        event.aggregateId = transactionId;
        event.occurredOn = date;
        return event;
    }
}
