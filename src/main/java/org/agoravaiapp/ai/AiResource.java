package org.agoravaiapp.ai;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agoravaiapp.common.Stubs;

/**
 * O servico de IA ainda nao foi criado. Estes endpoints respondem 501 ate a
 * integracao real (Fase 6) ser implementada.
 */
@Path("/api/v1/ai")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AiResource {

    @POST
    @Path("/insights")
    public Response insights() {
        return Stubs.underDevelopment("Insights com IA");
    }

    @POST
    @Path("/nlp/parse-transaction")
    public Response parseTransaction() {
        return Stubs.underDevelopment("NLP de lancamentos");
    }
}
