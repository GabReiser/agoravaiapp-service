package org.agoravaiapp.common;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Estabelece a identidade da requisicao a partir do ID token do Firebase.
 *
 * <h2>Por que isto mudou</h2>
 * <p>Antes este filtro lia {@code X-User-Id} / {@code X-User-Admin} do cliente e,
 * na ausencia deles, caia num usuario fixo ({@code dev-user}) em qualquer
 * ambiente. Como o frontend chama o core direto (nao existe gateway), o efeito
 * pratico era duplo e grave: <em>todos</em> os usuarios compartilhavam
 * {@code user_id = "dev-user"}, e qualquer pessoa podia ler ou escrever dados de
 * outra conta mandando {@code X-User-Id: <uid-alheio>} -- ou virar administrador
 * com {@code X-User-Admin: true}.</p>
 *
 * <p>Agora o core valida o token por conta propria (assinatura via JWKS do Google,
 * issuer, audience e expiracao, configurados em {@code application.properties}) e
 * deriva tudo dos claims. Headers do cliente sao <strong>ignorados</strong>.</p>
 *
 * <h2>Compatibilidade com um gateway futuro</h2>
 * <p>A arquitetura prevista em {@code docs/arquitetura-core-quarkus.md} coloca a
 * validacao num gateway. Validar aqui tambem nao conflita com isso: o gateway
 * continua podendo validar na borda, e o core deixa de depender de um componente
 * que ainda nao existe. Defesa em profundidade, nao trabalho duplicado -- a
 * verificacao local custa uma checagem de assinatura com chave em cache.</p>
 *
 * <h2>Fallback de desenvolvimento</h2>
 * <p>Sob {@code %dev} (e apenas la) {@code agoravai.auth.dev-fallback=true}
 * permite chamar o core com {@code curl} sem token. Em hom/prod a propriedade e
 * {@code false} e uma requisicao sem token nao passa.</p>
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class UserContextFilter implements ContainerRequestFilter {

    /** Codigo estavel que o frontend usa para redirecionar para a tela de confirmacao. */
    public static final String EMAIL_NOT_VERIFIED_CODE = "EMAIL_NOT_VERIFIED";

    private static final String CLAIM_EMAIL_VERIFIED = "email_verified";
    private static final String CLAIM_ADMIN = "admin";

    /** RFC 7807. Jakarta REST nao tem constante para este media type. */
    private static final String PROBLEM_JSON = "application/problem+json";

    @Inject
    UserContext userContext;

    @Inject
    SecurityIdentity identity;

    @ConfigProperty(name = "agoravai.auth.dev-fallback", defaultValue = "false")
    boolean devFallbackEnabled;

    @ConfigProperty(name = "agoravai.auth.dev-user-id", defaultValue = "dev-user")
    String devUserId;

    @ConfigProperty(name = "agoravai.auth.dev-admin", defaultValue = "false")
    boolean devAdmin;

    /**
     * Ponte temporaria para o painel admin.
     *
     * <p>O caminho definitivo e um custom claim {@code admin} gravado pelo
     * auth-service via Firebase Admin SDK (coluna {@code users.is_admin} ja
     * existe la). Enquanto o Admin SDK nao entrar, esta allowlist de UIDs por
     * configuracao mantem {@code /api/v1/admin/metrics} acessivel sem reabrir o
     * buraco do header {@code X-User-Admin}. Basta apagar esta propriedade
     * quando o claim existir -- {@link #resolveAdmin} ja prioriza o claim.</p>
     */
    @ConfigProperty(name = "agoravai.auth.admin-uids")
    Optional<Set<String>> adminUids;

    @Override
    public void filter(ContainerRequestContext ctx) {
        // Autenticacao proativa do Quarkus ja rodou neste ponto. Anonimo significa
        // que nao havia Bearer token (ou o path e publico).
        if (identity.isAnonymous() || !(identity.getPrincipal() instanceof JsonWebToken jwt)) {
            applyDevFallbackOrReject(ctx);
            return;
        }

        String userId = jwt.getSubject();
        if (userId == null || userId.isBlank()) {
            abort(ctx, Response.Status.UNAUTHORIZED, "INVALID_TOKEN", "Token sem claim 'sub'.");
            return;
        }

        boolean emailVerified = Boolean.TRUE.equals(jwt.getClaim(CLAIM_EMAIL_VERIFIED));

        userContext.init(userId, resolveAdmin(jwt, userId), emailVerified);

        // Politica do produto: bloqueio total ate a confirmacao do e-mail. O core
        // nao tem endpoint equivalente a GET /users/me, entao nao ha excecao aqui.
        if (!emailVerified) {
            abort(ctx, Response.Status.FORBIDDEN, EMAIL_NOT_VERIFIED_CODE,
                    "Confirme seu e-mail para usar o Agora Vai.");
        }
    }

    /**
     * Privilegio de admin vem de custom claim assinado pelo Firebase (nunca de
     * header). A allowlist de UIDs e o fallback enquanto o claim nao existe.
     */
    private boolean resolveAdmin(JsonWebToken jwt, String userId) {
        if (Boolean.TRUE.equals(jwt.getClaim(CLAIM_ADMIN))) {
            return true;
        }
        return adminUids.map(uids -> uids.contains(userId)).orElse(false);
    }

    private void applyDevFallbackOrReject(ContainerRequestContext ctx) {
        if (!devFallbackEnabled) {
            abort(ctx, Response.Status.UNAUTHORIZED, "INVALID_TOKEN",
                    "Requisicao sem ID token do Firebase.");
            return;
        }
        // Em dev tratamos o usuario fixo como verificado, senao seria impossivel
        // exercitar os endpoints sem passar pelo fluxo de e-mail.
        userContext.init(devUserId, devAdmin, true);
    }

    /**
     * Resposta de erro no formato RFC 7807 usado pelo resto da API, com a
     * propriedade {@code code} que o frontend consome em {@code lib/api.ts}.
     */
    private void abort(ContainerRequestContext ctx, Response.Status status, String code, String detail) {
        ctx.abortWith(Response.status(status)
                .type(PROBLEM_JSON)
                .entity(Map.of(
                        "type", "about:blank",
                        "title", status.getReasonPhrase(),
                        "status", status.getStatusCode(),
                        "detail", detail,
                        "code", code))
                .build());
    }
}
