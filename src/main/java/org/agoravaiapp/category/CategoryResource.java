package org.agoravaiapp.category;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/v1/categories")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    CategoryRepository repository;

    @GET
    public List<CategoryDto> list() {
        return repository.listAllOrdered().stream().map(CategoryDto::from).toList();
    }
}
