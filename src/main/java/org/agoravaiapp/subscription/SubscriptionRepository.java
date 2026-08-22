package org.agoravaiapp.subscription;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SubscriptionRepository implements PanacheRepositoryBase<Subscription, UUID> {

    public List<Subscription> listByUser(String userId) {
        return list("userId", Sort.ascending("name"), userId);
    }
}
