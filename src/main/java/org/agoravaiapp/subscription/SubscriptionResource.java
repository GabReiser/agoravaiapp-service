package org.agoravaiapp.subscription;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
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

@Path("/api/v1/subscriptions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SubscriptionResource {

    @Inject
    SubscriptionRepository repository;

    @Inject
    UserContext userContext;

    @GET
    public List<SubscriptionDto> list() {
        return repository.listByUser(userContext.userId()).stream().map(SubscriptionDto::from).toList();
    }

    @POST
    @Transactional
    public Response create(@Valid SubscriptionRequest request) {
        Subscription subscription = new Subscription();
        subscription.userId = userContext.userId();
        subscription.name = request.name();
        subscription.category = request.category();
        subscription.amount = request.amount();
        subscription.billingDay = request.billingDay() == null ? 1 : request.billingDay();
        subscription.active = request.active() == null || request.active();
        repository.persist(subscription);

        return Response.created(URI.create("/api/v1/subscriptions/" + subscription.id))
                .entity(SubscriptionDto.from(subscription))
                .build();
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    public SubscriptionDto patch(@PathParam("id") UUID id, @Valid SubscriptionPatchRequest request) {
        Subscription subscription = findOwned(id);

        if (request.name() == null && request.category() == null && request.amount() == null
                && request.billingDay() == null && request.active() == null) {
            throw new BadRequestException("Informe ao menos um campo para atualizar.");
        }

        if (request.name() != null) {
            subscription.name = request.name();
        }
        if (request.category() != null) {
            subscription.category = request.category();
        }
        if (request.amount() != null) {
            subscription.amount = request.amount();
        }
        if (request.billingDay() != null) {
            subscription.billingDay = request.billingDay();
        }
        if (request.active() != null) {
            subscription.active = request.active();
        }

        repository.persist(subscription);
        return SubscriptionDto.from(subscription);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Subscription subscription = findOwned(id);
        repository.delete(subscription);
        return Response.noContent().build();
    }

    private Subscription findOwned(UUID id) {
        Subscription subscription = repository.findById(id);
        if (subscription == null || !subscription.userId.equals(userContext.userId())) {
            throw new NotFoundException("Assinatura nao encontrada.");
        }
        return subscription;
    }
}
