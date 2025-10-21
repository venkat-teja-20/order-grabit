package com.grabit.bean.restaurant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BranchDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("building")
    private String building;

    @JsonProperty("address_line")
    private String addressLine;

    @JsonProperty("land_mark")
    private String landMark;

    @JsonProperty("pin_code")
    private String pinCode;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("branch_name")
    private String branchName;

    @JsonProperty("open_time")
    private LocalDateTime openTime;

    @JsonProperty("close_time")
    private LocalDateTime closeTime;

    @JsonProperty("food_serve_type")
    private String foodServeType;

    @JsonProperty("orders_received")
    private Long ordersReceived;

    @JsonProperty("branch_status")
    private String branchStatus;

    @JsonProperty("branch_activity")
    private String branchActivity;

    @JsonProperty("restaurant_id")
    private Long restaurant_id;

    @JsonProperty("food_items_list")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FoodItemDTO> foodItemDTOList=new ArrayList<>();
}
