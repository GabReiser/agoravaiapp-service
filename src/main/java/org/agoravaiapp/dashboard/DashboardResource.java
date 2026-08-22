package org.agoravaiapp.dashboard;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.Optional;
import org.agoravaiapp.common.Dates;
import org.agoravaiapp.common.UserContext;

@Path("/api/v1/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @Inject
    DashboardService service;

    @Inject
    DashboardCache cache;

    @Inject
    UserContext userContext;

    @GET
    public DashboardResponse get(@QueryParam("from") String from, @QueryParam("to") String to) {
        LocalDate fromDate = Dates.parse(from, "from");
        LocalDate toDate = Dates.parse(to, "to");
        if (fromDate == null) {
            fromDate = LocalDate.now().withDayOfMonth(1).minusMonths(5);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }

        String userId = userContext.userId();
        Optional<DashboardResponse> cached = cache.get(userId);
        if (cached.isPresent()) {
            return cached.get();
        }
        DashboardResponse response = service.build(userId, fromDate, toDate);
        cache.put(userId, response);
        return response;
    }
}
