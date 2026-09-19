package org.agoravaiapp.common;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.ForbiddenException;

/**
 * Estado do usuario autenticado para a requisicao corrente.
 *
 * <p>Populado por {@link UserContextFilter} a partir do ID token do Firebase,
 * que o proprio core valida (assinatura, issuer, audience e expiracao). Nenhum
 * campo aqui vem de header enviado pelo cliente em producao -- ver a nota de
 * seguranca em {@link UserContextFilter}.</p>
 *
 * <p>{@code userId} e o UID do Firebase (claim {@code sub}), que e a identidade
 * de rede compartilhada entre os servicos. O auth-service mantem um UUID interno
 * proprio na tabela {@code users}, mas ele nunca atravessa a fronteira: o core
 * grava {@code transactions.user_id} com o UID, sem precisar consultar o
 * auth-service para resolver identidade.</p>
 */
@RequestScoped
public class UserContext {

    private String userId;
    private boolean admin;
    private boolean emailVerified;

    void init(String userId, boolean admin, boolean emailVerified) {
        this.userId = userId;
        this.admin = admin;
        this.emailVerified = emailVerified;
    }

    public String userId() {
        if (userId == null || userId.isBlank()) {
            throw new ForbiddenException("Usuario nao identificado na requisicao.");
        }
        return userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    /** Reflete o claim {@code email_verified} do ID token. */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void requireAdmin() {
        if (!admin) {
            throw new ForbiddenException("Acesso restrito a administradores.");
        }
    }
}
