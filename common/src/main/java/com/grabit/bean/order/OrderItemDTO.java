package com.grabit.bean.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class OrderItemDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("item_id")
    private Long itemId;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("item_quantity")
    private Integer itemQuantity;

    @JsonProperty("item_price")
    private Double itemPrice;

    @JsonProperty("item_original_price")
    private Double itemOriginalPrice;

    @JsonProperty("items_total_cost")
    private Double itemsTotalCost;
}
