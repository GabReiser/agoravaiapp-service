package org.agoravaiapp.admin;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.math.BigDecimal;
import org.agoravaiapp.common.UserContext;

@Path("/api/v1/admin/metrics")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    EntityManager em;

    @Inject
    UserContext userContext;

    @GET
    @Transactional
    public MetricsResponse metrics() {
        userContext.requireAdmin();

        long transactions = count("SELECT COUNT(*) FROM core.transactions");
        long subscriptions = count("SELECT COUNT(*) FROM core.subscriptions");
        long activeSubscriptions = count("SELECT COUNT(*) FROM core.subscriptions WHERE active = true");
        long users = count("SELECT COUNT(DISTINCT user_id) FROM core.transactions");

        Object[] totals = (Object[]) em.createNativeQuery("""
                        SELECT
                            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0),
                            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0)
                        FROM core.transactions
                        """)
                .getSingleResult();

        return new MetricsResponse(
                transactions,
                subscriptions,
                activeSubscriptions,
                users,
                (BigDecimal) totals[0],
                (BigDecimal) totals[1]);
    }

    private long count(String sql) {
        Number value = (Number) em.createNativeQuery(sql).getSingleResult();
        return value.longValue();
    }
}
