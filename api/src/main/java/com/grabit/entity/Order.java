package com.grabit.entity;

import com.grabit.enums.OrderStatus;
import com.grabit.enums.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Orders")
@Setter
@Getter
@Table(name = "Orders")
public class Order extends AuditDetails<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id",nullable = false,updatable = false)
    @NotNull
    private Long restaurantId;

    @Column(name = "branch_id",nullable = false,updatable = false)
    @NotNull
    private Long branchId;

    @Column(name = "restaurant_name",nullable = false,
            updatable = false,length = 50)
    @NotEmpty
    @Size(min = 3, max = 50)
    private String restaurantName;

    @Column(name = "branch_name",nullable = false,
            updatable = false,length = 50)
    @NotEmpty
    @Size(min = 3, max = 50)
    private String branchName;

    @Column(name = "member_id",nullable = false,updatable = false)
    @NotNull
    private Long memberId;

    @Column(name = "gst",nullable = false,updatable = false)
    @NotNull
    private Double gst;

    @Column(name = "delivery_charges",nullable = false,updatable = false)
    @NotNull
    private Double deliveryCharges;

    @Column(name = "tip_amount")
    private Double tipAmount= (double) 0;

    @Column(name = "total_price",nullable = false,updatable = false)
    @NotNull
    private Double totalPrice;

    @Column(name = "payment_method",nullable = false,updatable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "order_status",nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "food_specifications")
    private String foodSpecifications;

    @Column(name = "delivery_partner_id")
    private Long deliveryPartnerId;

    @Column(name = "order_start_time",updatable = false,nullable = false)
    @NotNull
    private OffsetDateTime orderStartTime;

    @Column(name = "order_delivered_time")
    private OffsetDateTime orderDeliveredTime;

    @Column(name = "delivery_time")
    private Long deliveryTime;

    @Column(name = "delivery_instructions")
    private String deliveryInstructions;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private List<OrderItem> orderItems=new ArrayList<>();
}
