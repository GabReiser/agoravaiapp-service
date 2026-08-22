package org.agoravaiapp.statement;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agoravaiapp.common.Stubs;

@Path("/api/v1/transactions/statement")
@Produces(MediaType.APPLICATION_JSON)
public class StatementResource {

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload() {
        return Stubs.underDevelopment("Importacao de extrato");
    }

    @POST
    @Path("/{statementId}/confirm")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response confirm(@PathParam("statementId") String statementId) {
        return Stubs.underDevelopment("Confirmacao de conciliacao de extrato");
    }
}
