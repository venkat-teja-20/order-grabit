package com.grabit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "OrderItem")
@Setter
@Getter
@Table(name = "OrderItem")
public class OrderItem extends AuditDetails<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false, updatable = false)
    @NotNull
    private Long itemId;

    @Column(name = "item_name", nullable = false,
            updatable = false, length = 50)
    @NotEmpty
    @Size(max = 50)
    private String itemName;

    @Column(name = "item_quantity", nullable = false, updatable = false)
    @NotNull
    private Integer itemQuantity;

    @Column(name = "item_price", nullable = false, updatable = false)
    @NotNull
    private Double itemPrice;

    @Column(name = "item_original_price", nullable = false, updatable = false)
    @NotNull
    private Double itemOriginalPrice;

    @Column(name = "items_total_cost", nullable = false, updatable = false)
    @NotNull
    private Double itemsTotalCost;

    @ManyToOne
    @JoinColumn(name = "orderId", nullable = false)
    @JsonIgnore
    private Order order;

    @PrePersist
    public void getFinalCostOfItems() {
        this.itemsTotalCost = this.itemQuantity * this.itemPrice;
    }
}
