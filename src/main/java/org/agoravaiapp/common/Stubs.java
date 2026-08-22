package org.agoravaiapp.common;

import jakarta.ws.rs.core.Response;
import java.util.Map;

public final class Stubs {

    private Stubs() {
    }

    /** Resposta padrao para funcionalidades ainda em desenvolvimento (HTTP 501). */
    public static Response underDevelopment(String feature) {
        return Response.status(Response.Status.NOT_IMPLEMENTED)
                .entity(Map.of(
                        "status", 501,
                        "title", "Em desenvolvimento",
                        "detail", feature + " ainda nao foi implementado."))
                .build();
    }
}
