package org.agoravaiapp.quickaction;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.agoravaiapp.common.UserContext;

@Path("/api/v1/quick-actions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QuickActionResource {

    @Inject
    QuickActionRepository repository;

    @Inject
    UserContext userContext;

    @GET
    public List<QuickActionDto> list() {
        return repository.listByUser(userContext.userId()).stream().map(QuickActionDto::from).toList();
    }

    @POST
    @Transactional
    public Response create(@Valid QuickActionRequest request) {
        QuickAction quickAction = new QuickAction();
        quickAction.userId = userContext.userId();
        quickAction.label = request.label();
        quickAction.icon = request.icon();
        quickAction.amount = request.amount();
        quickAction.category = request.category();
        repository.persist(quickAction);

        return Response.created(URI.create("/api/v1/quick-actions/" + quickAction.id))
                .entity(QuickActionDto.from(quickAction))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        QuickAction quickAction = repository.findById(id);
        if (quickAction == null || !quickAction.userId.equals(userContext.userId())) {
            throw new NotFoundException("Atalho nao encontrado.");
        }
        repository.delete(quickAction);
        return Response.noContent().build();
    }
}
