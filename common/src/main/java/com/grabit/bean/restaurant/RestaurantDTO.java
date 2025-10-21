package com.grabit.bean.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("restaurant_name")
    private String restaurantName;

    @JsonProperty("total_orders")
    private Long totalOrders;

    @JsonProperty("branch_details")
    private List<BranchDTO> branchDTOList=new ArrayList<>();
}
