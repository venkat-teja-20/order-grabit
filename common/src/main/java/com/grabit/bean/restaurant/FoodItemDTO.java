package com.grabit.bean.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodItemDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("item_name")
    private String itemName;

    @JsonProperty("serve_count")
    private Integer serveCount;

    @JsonProperty("description")
    private String foodDescription;

    @JsonProperty("category")
    private String category;

    @JsonProperty("item_status")
    private String itemStatus;

    @JsonProperty("item_type")
    private String itemType;

    @JsonProperty("item_price")
    private Integer itemPrice;

    @JsonProperty("item_quantity")
    private Integer itemQuantity;

    @JsonProperty("item_original_price")
    private Double itemOriginalPrice;

    @JsonProperty("image")
    private String image;

    @JsonProperty("order_count")
    private Long orderCount;

    @JsonProperty("quantity_sold")
    private Long quantitySold;

    @JsonProperty("branch_id")
    private Long branchId;
}
