package org.agoravaiapp.outbox;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Feed de eventos de dominio para consumo entre servicos.
 *
 * <p>Nao e um endpoint de usuario: quem chama e a gamificacao, em nome de
 * ninguem. Por isso nao ha ID token aqui -- a autenticacao e um segredo
 * compartilhado, e o caminho fica fora da politica {@code authenticated} de
 * {@code /api/v1/*} (ver {@code application.properties}).</p>
 *
 * <p>O payload carrega apenas quem, qual objeto e em que dia. Sem valor, sem
 * descricao, sem categoria: o consumidor pontua comportamento, e um vazamento
 * deste feed nao deve revelar quanto alguem gastou.</p>
 */
@Path("/api/v1/internal/events")
@Produces(MediaType.APPLICATION_JSON)
public class InternalEventsResource {

    /**
     * Idade minima de um evento para ser entregue.
     *
     * <p>Resolve a armadilha do cursor. O id vem de uma sequence e e atribuido no
     * INSERT, mas a linha so aparece no COMMIT: duas transacoes concorrentes
     * podem pegar 100 e 101 e commitar fora de ordem. Um leitor que visse 101 e
     * avancasse o cursor perderia 100 para sempre, porque ela so ficou visivel
     * depois.</p>
     *
     * <p>Entregar apenas o que ja tem alguns segundos garante que toda transacao
     * concorrente daquele intervalo ja commitou ou abortou. O custo e um atraso
     * de segundos no XP, irrelevante aqui; o beneficio e nao perder evento.</p>
     */
    private static final int MIN_AGE_SECONDS = 5;

    private static final int MAX_LIMIT = 500;

    @ConfigProperty(name = "agoravai.internal.shared-secret")
    String sharedSecret;

    @GET
    public List<EventDto> events(
            @HeaderParam("X-Internal-Secret") String secret,
            @QueryParam("since") @DefaultValue("0") long since,
            @QueryParam("limit") @DefaultValue("200") int limit) {

        requireValidSecret(secret);

        Instant cutoff = Instant.now().minus(MIN_AGE_SECONDS, ChronoUnit.SECONDS);
        int safeLimit = Math.clamp(limit, 1, MAX_LIMIT);

        List<OutboxEvent> events = OutboxEvent
                .find("id > ?1 and createdAt < ?2 order by id", since, cutoff)
                .page(0, safeLimit)
                .list();

        return events.stream().map(EventDto::from).toList();
    }

    /**
     * Comparacao em tempo constante.
     *
     * <p>{@code String.equals} sai no primeiro caractere diferente, e a diferenca
     * de tempo e mensuravel em volume -- permitiria descobrir o segredo caractere
     * a caractere em vez de adivinha-lo inteiro.</p>
     */
    private void requireValidSecret(String provided) {
        if (provided == null || sharedSecret == null || sharedSecret.isBlank()) {
            throw new ForbiddenException("Acesso interno negado.");
        }
        boolean matches = MessageDigest.isEqual(
                provided.getBytes(StandardCharsets.UTF_8),
                sharedSecret.getBytes(StandardCharsets.UTF_8));

        if (!matches) {
            throw new ForbiddenException("Acesso interno negado.");
        }
    }

    public record EventDto(
            long id,
            String eventType,
            String userId,
            String aggregateId,
            LocalDate occurredOn) {

        static EventDto from(OutboxEvent event) {
            return new EventDto(
                    event.id,
                    event.eventType,
                    event.userId,
                    event.aggregateId,
                    event.occurredOn);
        }
    }
}
