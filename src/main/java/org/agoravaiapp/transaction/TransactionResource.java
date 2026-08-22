package org.agoravaiapp.transaction;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import org.agoravaiapp.category.Category;
import org.agoravaiapp.category.CategoryRepository;
import org.agoravaiapp.common.Dates;
import org.agoravaiapp.common.PageResponse;
import org.agoravaiapp.common.UserContext;

@Path("/api/v1/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    TransactionRepository repository;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    UserContext userContext;

    @GET
    @Transactional
    public PageResponse<TransactionDto> list(
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        var fromDate = Dates.parse(from, "from");
        var toDate = Dates.parse(to, "to");
        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, Math.min(size, 100));

        var items = repository.findByUser(userContext.userId(), fromDate, toDate, safePage, safeSize)
                .stream()
                .map(TransactionDto::from)
                .toList();
        long total = repository.countByUser(userContext.userId(), fromDate, toDate);
        int totalPages = (int) Math.ceil((double) total / safeSize);
        return new PageResponse<>(items, safePage, safeSize, total, totalPages);
    }

    @POST
    @Transactional
    public Response create(@Valid CreateTransactionRequest request) {
        Category category = categoryRepository.findById(request.categoryId());
        if (category == null) {
            throw new NotFoundException("Categoria nao encontrada: " + request.categoryId());
        }

        Transaction transaction = new Transaction();
        transaction.userId = userContext.userId();
        transaction.category = category;
        transaction.description = request.description();
        transaction.amount = request.amount();
        transaction.type = request.type();
        transaction.date = request.date();
        transaction.source = request.source() == null || request.source().isBlank()
                ? "MANUAL"
                : request.source();
        repository.persist(transaction);
        repository.flush();

        return Response.created(URI.create("/api/v1/transactions/" + transaction.id))
                .entity(TransactionDto.from(transaction))
                .build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        Transaction transaction = repository.findById(id);
        if (transaction == null || !transaction.userId.equals(userContext.userId())) {
            throw new NotFoundException("Lancamento nao encontrado.");
        }
        repository.delete(transaction);
        return Response.noContent().build();
    }
}
