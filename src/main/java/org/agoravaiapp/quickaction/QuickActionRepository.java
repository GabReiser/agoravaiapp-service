package org.agoravaiapp.quickaction;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class QuickActionRepository implements PanacheRepositoryBase<QuickAction, UUID> {

    public List<QuickAction> listByUser(String userId) {
        return list("userId", Sort.ascending("label"), userId);
    }
}
