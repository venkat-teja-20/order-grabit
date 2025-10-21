package com.grabit.bean.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.grabit.enums.OrderStatus;
import com.grabit.enums.PaymentMethod;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class OrderDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("branch_id")
    private Long branchId;

    @JsonProperty("restaurant_name")
    private String restaurantName;

    @JsonProperty("branch_name")
    private String branchName;

    @JsonProperty("member_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long memberId;

    @JsonProperty("gst")
    private Double gst;

    @JsonProperty("delivery_charges")
    private Double deliveryCharges;

    @JsonProperty("tip_amount")
    private Double tipAmount= (double) 0;

    @JsonProperty("total_price")
    private Double totalPrice;

    @JsonProperty("payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @JsonProperty("order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @JsonProperty("delivery_partner_id")
    private Long deliveryPartnerId;

    @JsonProperty("order_start_time")
    private OffsetDateTime orderStartTime;

    @JsonProperty("order_delivered_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OffsetDateTime orderDeliveredTime;

    @JsonProperty("delivery_time")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long deliveryTime;

    @JsonProperty("order_items")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<OrderItemDTO> orderItemDTOList=new ArrayList<>();
}
