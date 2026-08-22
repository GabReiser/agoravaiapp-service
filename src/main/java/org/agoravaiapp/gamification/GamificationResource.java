package org.agoravaiapp.gamification;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agoravaiapp.common.Stubs;

/**
 * O servico de gamificacao ainda nao foi criado. Estes endpoints respondem 501
 * ate o servico real (reativo via Kafka) existir.
 */
@Path("/api/v1/gamification")
@Produces(MediaType.APPLICATION_JSON)
public class GamificationResource {

    @GET
    @Path("/profile")
    public Response profile() {
        return Stubs.underDevelopment("Perfil de gamificacao");
    }

    @GET
    @Path("/missions")
    public Response missions() {
        return Stubs.underDevelopment("Missoes");
    }

    @GET
    @Path("/ranking")
    public Response ranking() {
        return Stubs.underDevelopment("Ranking");
    }

    @GET
    @Path("/rewards")
    public Response rewards() {
        return Stubs.underDevelopment("Recompensas");
    }

    @GET
    @Path("/tiers")
    public Response tiers() {
        return Stubs.underDevelopment("Ligas");
    }
}
