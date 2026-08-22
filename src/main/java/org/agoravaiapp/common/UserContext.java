package org.agoravaiapp.common;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.ForbiddenException;

/**
 * Estado do usuario autenticado para a requisicao corrente.
 *
 * <p>O gateway valida o JWT (Firebase) e injeta {@code X-User-Id} (e, quando
 * houver, {@code X-User-Admin}). Em dev, sem gateway, usamos um usuario fixo
 * configurado em {@code agoravai.auth.dev-user-id}.</p>
 */
@RequestScoped
public class UserContext {

    private String userId;
    private boolean admin;

    void init(String userId, boolean admin) {
        this.userId = userId;
        this.admin = admin;
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

    public void requireAdmin() {
        if (!admin) {
            throw new ForbiddenException("Acesso restrito a administradores.");
        }
    }
}
