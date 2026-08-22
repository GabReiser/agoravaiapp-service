package org.agoravaiapp.quickaction;

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
@Table(name = "quick_actions")
public class QuickAction extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(name = "user_id", nullable = false)
    public String userId;

    @Column(nullable = false)
    public String label;

    public String icon;

    @Column(precision = 18, scale = 2)
    public BigDecimal amount;

    public String category;
}
