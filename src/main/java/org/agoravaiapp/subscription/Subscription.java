package org.agoravaiapp.subscription;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
public class Subscription extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(nullable = false)
    public String name;

    public String category;

    @Column(nullable = false, precision = 18, scale = 2)
    public BigDecimal amount;

    @Column(name = "billing_day", nullable = false)
    public int billingDay = 1;

    @Column(nullable = false)
    public boolean active = true;
}
