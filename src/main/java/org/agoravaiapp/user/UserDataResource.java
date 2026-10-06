package org.agoravaiapp.user;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agoravaiapp.common.UserContext;
import org.jboss.logging.Logger;

/**
 * Apagamento dos dados do usuario neste servico (LGPD, direito de eliminacao).
 *
 * <p>Quem orquestra e o auth-service: ele chama este endpoint repassando o
 * <strong>ID token do proprio usuario</strong>, e so depois remove a linha em
 * {@code users} e a conta no Firebase. Repassar o token evita service account e
 * segredo compartilhado, e mantem a garantia mais importante aqui: este endpoint
 * nao recebe um identificador de quem apagar -- ele apaga <em>exclusivamente</em>
 * o dono do token. Nao existe forma de pedir a exclusao de outra pessoa.</p>
 *
 * <p>A ordem entre os servicos importa: os dados do core saem primeiro, enquanto
 * o token ainda vale. Apagar a conta no Firebase antes deixaria estes registros
 * orfaos, sem token que autorizasse remove-los depois.</p>
 */
@Path("/api/v1/users/me/data")
@Produces(MediaType.APPLICATION_JSON)
public class UserDataResource {

    private static final Logger log = Logger.getLogger(UserDataResource.class);

    @Inject
    EntityManager em;

    @Inject
    UserContext userContext;

    /**
     * Remove tudo que pertence ao usuario autenticado.
     *
     * <p>Idempotente de proposito: repetir a chamada devolve 200 com zero
     * removidos, em vez de erro. Exclusao e um fluxo que costuma ser repetido
     * apos falha de rede, e nao ha estado intermediario para conciliar.</p>
     */
    @DELETE
    @Transactional
    public Response deleteMyData() {
        String userId = userContext.userId();

        // As transacoes saem antes dos extratos: transactions.statement_id aponta
        // para statements, e a ordem inversa violaria a integridade referencial.
        long transactions = deleteFrom("transactions", userId);
        long statements = deleteFrom("statements", userId);
        long subscriptions = deleteFrom("subscriptions", userId);
        long quickActions = deleteFrom("quick_actions", userId);

        log.infof(
                "Dados apagados do core -- uid=%s transacoes=%d extratos=%d assinaturas=%d atalhos=%d",
                userId, transactions, statements, subscriptions, quickActions);

        return Response.ok(new DeletionSummary(transactions, statements, subscriptions, quickActions))
                .build();
    }

    /**
     * O nome da tabela e concatenado, mas nunca vem de entrada externa -- sao os
     * quatro literais acima. O {@code userId} vai como parametro, jamais
     * concatenado.
     */
    private long deleteFrom(String table, String userId) {
        return em.createNativeQuery("DELETE FROM core." + table + " WHERE user_id = ?1")
                .setParameter(1, userId)
                .executeUpdate();
    }

    /** Quantos registros sairam de cada tabela; vai para o log do auth-service. */
    public record DeletionSummary(
            long transactions,
            long statements,
            long subscriptions,
            long quickActions) {
    }
}
