package com.grabit.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grabit.Feign.DeliveryPartnerInterface;
import com.grabit.Feign.MemberInterface;
import com.grabit.Feign.RestaurantInterface;
import com.grabit.Utilities.ModelMapperUtility;
import com.grabit.Utilities.Utility;
import com.grabit.bean.restaurant.FoodItemDTO;
import com.grabit.bean.restaurant.RestaurantDTO;
import com.grabit.entity.Order;
import com.grabit.entity.OrderItem;
import com.grabit.enums.CommonErrors;
import com.grabit.enums.OrderStatus;
import com.grabit.exception.CustomException;
import com.grabit.mapper.DeliveryPartnerURLMapper;
import com.grabit.mapper.MemberURLMapper;
import com.grabit.mapper.RestaurantURLMapper;
import com.grabit.bean.order.OrderDTO;
import com.grabit.bean.order.OrderItemDTO;
import com.grabit.repository.OrderRepository;
import com.squareup.okhttp.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MemberInterface memberInterface;

    @Autowired
    RestaurantInterface restaurantInterface;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private RestaurantURLMapper restaurantURLMapper;

    @Autowired
    private MemberURLMapper memberURLMapper;

    @Autowired
    private DeliveryPartnerInterface deliveryPartnerInterface;

    @Autowired
    private DeliveryPartnerURLMapper deliveryPartnerURLMapper;

    private OkHttpClient client=new OkHttpClient();

    @Transactional
    public OrderDTO placeOrder(OrderDTO request, String memberId){
        //member check condition
        //Map<String,Object> memberCheckResult= memberInterface.checkIfMemberExists(memberId);
        String url = memberURLMapper.getMemberExistsURL(memberId);
        Map<String,Object> memberCheckResult= restTemplate.getForObject(url,Map.class);
        if(Utility.isNullOrEmpty(memberCheckResult) || !Boolean.parseBoolean(String.valueOf(memberCheckResult.get("is_present")))){
            throw new CustomException(Utility.buildErrorObject("MEMBER_NOT_FOUND","Member doesn't exists with provided member id",404,"placeOrder"));
        }

        //restaurant & branch check condition
//        Map<String,Object> restaurantCheckResult=restaurantInterface.checkRestaurantExists(String.valueOf(request.getRestaurantId()));
        String restaurantUrl = restaurantURLMapper.getRestaurantExistsURL(String.valueOf(request.getRestaurantId()));
        Map<String,Object> restaurantCheckResult= restTemplate.getForObject(restaurantUrl,Map.class);
        if(Utility.isNullOrEmpty(restaurantCheckResult) || !(boolean) restaurantCheckResult.get("is_present"))
            throw new CustomException(Utility.buildErrorObject("RESTAURANT_NOT_FOUND","Restaurant doesn't exists with provided restaurant id",404,"placeOrder"));

//        Map<String,String> restaurantAndBranchCheckResult=restaurantInterface.getRestaurantAndBranchName(String.valueOf(request.getRestaurantId()),String.valueOf(request.getBranchId()));
        String restaurantAndBranchDetailsURL = restaurantURLMapper.getRestaurantAndBranchNamesURL(request.getRestaurantId(),request.getBranchId());
        Map<String,String> restaurantAndBranchDetails= restTemplate.getForObject(restaurantAndBranchDetailsURL,Map.class);
        if(Utility.isNullOrEmpty(restaurantAndBranchDetails))
            throw new CustomException(Utility.buildErrorObject("BRANCH_NOT_FOUND","Branch doesn't exists with provided branch id",404,"placeOrder"));

        List<FoodItemDTO> foodItemDTOList=new ArrayList<>();
        List<Map> itemDetailsList=new ArrayList<>();
        //item check condition
        request.getOrderItemDTOList().parallelStream().forEach(orderItemDTO -> {
            Long itemId=orderItemDTO.getItemId();
//            Map<String,Object> itemCheckResult=restaurantInterface.checkFoodItemExistsInABranch(String.valueOf(orderItemDTO.getItemId()),String.valueOf(request.getBranchId()));
            log.info("Food Item Id : "+itemId);
            String itemCheckUrl=restaurantURLMapper.getFoodItemExistsInABranchURL(itemId, request.getBranchId(),"true");
            Map<String,Object> itemCheckResult=restTemplate.getForObject(itemCheckUrl,Map.class);
            if(Utility.isNullOrEmpty(itemCheckResult) || Utility.isNullOrEmpty(itemCheckResult.get("item_details")) || !(boolean) itemCheckResult.get("is_present"))
                throw new CustomException(Utility.buildErrorObject("ITEM_NOT_FOUND","No Item found with Item Id : "+itemId,404,"placeOrder"));
            Map<String,Object> itemDetails= (Map<String, Object>) itemCheckResult.get("item_details");
            itemDetails.put("item_quantity",orderItemDTO.getItemQuantity());
            itemDetails.put("item_id",itemId);
            itemDetailsList.add(itemDetails);
            FoodItemDTO foodItemDTO=new FoodItemDTO();
            foodItemDTO.setId(itemId);
            foodItemDTO.setItemQuantity(orderItemDTO.getItemQuantity());
            foodItemDTOList.add(foodItemDTO);
        });

        Order order=setOrderDetails(request,new Order(),memberId,itemDetailsList,restaurantAndBranchDetails);
        Order savedOrder=orderRepository.save(order);
        // update restaurant & branch orders total orders
//        Map<String,Object> orderUpdateResult=restaurantInterface.updateRestaurantAndBranchOrders(String.valueOf(request.getRestaurantId()),String.valueOf(request.getBranchId()));
        String getRestaurantAndBranchOrdersCountUpdateUrl=restaurantURLMapper.getRestaurantAndBranchOrdersCountUpdateURL(request.getRestaurantId(),request.getBranchId());
        try{
            Request restaurantAndBranchOrderUpdateRequest=new Request.Builder()
                    .url(getRestaurantAndBranchOrdersCountUpdateUrl)
                    .patch(RequestBody.create(MediaType.parse("application/json"), Objects.requireNonNull(Utility.toJson(new HashMap<>()))))
                    .build();
            Response response=client.newCall(restaurantAndBranchOrderUpdateRequest).execute();
            if(Utility.isNullOrEmpty(Utility.isNullOrEmpty(response)))
                throw new CustomException(Utility.buildErrorObject("ORDERS_SYNC_FAILED","Error while updating restaurant and branch orders", 500,"placeOrder"));
            if(response.code()!=200)
                throw new CustomException(Utility.buildErrorObject("ORDERS_SYNC_FAILED",response.body().string(), response.code(),"placeOrder"));
        } catch (Exception e) {
            log.info(e);
            throw new CustomException(Utility.buildErrorObject("ORDERS_SYNC_FAILED","Order count sync to restaurant failed",500,"placeOrder"));
        }

        // update item quantity and order count
//        Map<String,Object> foodItemUpdateResult=restaurantInterface.updateFoodItemOrdersAndAvailability(foodItemDTOList);
        String getUpdateFoodItemOrdersAndAvailabilityUrl=restaurantURLMapper.getUpdateFoodItemOrdersAndAvailabilityURL();
        try{
            Request itemUpdateRequest=new Request.Builder()
                    .url(getUpdateFoodItemOrdersAndAvailabilityUrl)
                    .patch(RequestBody.create(MediaType.parse("application/json"), Utility.toJson(foodItemDTOList)))
                    .build();
            client.setConnectTimeout(5,TimeUnit.MINUTES);
            Response response= client.newCall(itemUpdateRequest).execute();
            if(Utility.isNullOrEmpty(Utility.isNullOrEmpty(response)))
                throw new CustomException(Utility.buildErrorObject("ITEMS_QUANTITY_SYNC_FAILED","Error while updating items quantity of food item", 500,"placeOrder"));
            if(response.code()!=200)
                throw new CustomException(Utility.buildErrorObject("ITEMS_QUANTITY_SYNC_FAILED",response.body().string(), response.code(),"placeOrder"));
        } catch (Exception e) {
            log.info(e);
            throw new CustomException(Utility.buildErrorObject("ITEMS_QUANTITY_SYNC_FAILED","Items quantity sync to restaurant failed",500,"placeOrder"));
        }

        log.info("Saved Order : "+ Utility.toJson(savedOrder));
        OrderDTO orderDTO=ModelMapperUtility.map(savedOrder,OrderDTO.class);
        orderDTO.setOrderItemDTOList(savedOrder.getOrderItems().stream().map(orderItem -> ModelMapperUtility.map(orderItem,OrderItemDTO.class)).toList());
        return orderDTO;
    }

    private Order setOrderDetails(OrderDTO request,Order order,String memberId,List<Map> itemDetailsList,Map<String,String> restaurantAndBranchDetails){
        BeanUtils.copyProperties(request,order);
        order.setMemberId(Long.valueOf(memberId));
        order.setRestaurantName(restaurantAndBranchDetails.get("restaurant_name"));
        order.setBranchName(restaurantAndBranchDetails.get("branch_name"));

        if(Utility.isNullOrEmpty(request.getOrderItemDTOList()))
            throw new CustomException(Utility.buildErrorObject(CommonErrors.ORDER_ITEMS_MISSING.toString(),CommonErrors.ORDER_ITEMS_MISSING.getMessage(), 400,"PlaceOrderService"));
        List<OrderItem> orderItemList=itemDetailsList.parallelStream().map(itemDetails->{
            OrderItem orderItem=new OrderItem();
            // Make the variables copy to entity dynamic
             /*Keep @serialized annotations in orderItemDto then copy map
             contents to orderItemDto and then from orderItemDto to orderItem*/
            orderItem.setItemName(String.valueOf(itemDetails.get("item_name")));
            orderItem.setItemPrice(Double.valueOf(String.valueOf(itemDetails.get("item_price"))));
            orderItem.setItemOriginalPrice(Double.valueOf(String.valueOf(itemDetails.get("item_original_price"))));
            orderItem.setItemId(Long.valueOf(String.valueOf(itemDetails.get("id"))));
            orderItem.setItemQuantity(Integer.valueOf(String.valueOf(itemDetails.get("item_quantity"))));
            orderItem.setOrder(order);
            return orderItem;
        }).toList();
        Double itemTotalPrice=orderItemList.stream().mapToDouble(OrderItem::getItemPrice).sum();
        order.setOrderStartTime(OffsetDateTime.now(ZoneOffset.UTC));
        order.setOrderItems(orderItemList);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setTotalPrice(itemTotalPrice+request.getGst()+request.getTipAmount());
        return order;
    }

    public List<OrderDTO> getAllOrders(String restaurantId,String branchId,int pageNumber,int pageSize,String orderBy,String orderField){
        //check valid restaurant id and branch id
        //Map<String,Object> restaurantCheckResult=restaurantInterface.checkRestaurantExists(String.valueOf(request.getRestaurantId()));
        String restaurantUrl = restaurantURLMapper.getRestaurantExistsURL(restaurantId);
        Map<String,Object> restaurantCheckResult= restTemplate.getForObject(restaurantUrl,Map.class);
        if(Utility.isNullOrEmpty(restaurantCheckResult) || !(boolean) restaurantCheckResult.get("is_present"))
            throw new CustomException(Utility.buildErrorObject("RESTAURANT_NOT_FOUND","Restaurant doesn't exists with provided restaurant id",404,"getAllOrders"));

        Sort sort="ASC".equals(orderBy)?Sort.by(orderField).ascending():Sort.by(orderBy).descending();
        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize,sort);
        Page<Order> orders=orderRepository.findAllByRestaurantIdAndBranchId(Long.valueOf(restaurantId),Long.valueOf(branchId),pageRequest);
        log.info(orders.getSize()+" Orders Fetched");
        List<OrderDTO> orderDTOList=orders.stream().map(order -> {
            OrderDTO orderDTO=ModelMapperUtility.map(order,OrderDTO.class);
            List<OrderItemDTO> orderItemDTOList=order.getOrderItems().stream().map(orderItem -> ModelMapperUtility.map(orderItem,OrderItemDTO.class)).toList();
            orderDTO.setOrderItemDTOList(orderItemDTOList);
            return orderDTO;
        }).toList();
        return orderDTOList;
    }

    public OrderDTO getSingleOrder(String orderId){
        if(!Utility.isNumeric(orderId))
            throw new CustomException(Utility.buildErrorObject("INVALID_ORDER_ID","Order Id Provided is Not Valid",400,"getSingleOrder"));
        Order order=orderRepository.findById(Long.valueOf(orderId)).orElseThrow(()->new EntityNotFoundException("Order Details Not found with Requested Order Id"));
        OrderDTO orderDTO=ModelMapperUtility.map(order,OrderDTO.class);
        List<OrderItemDTO> orderItemDTOList=order.getOrderItems().stream().map(orderItem -> ModelMapperUtility.map(orderItem,OrderItemDTO.class)).toList();
        orderDTO.setOrderItemDTOList(orderItemDTOList);
        return orderDTO;
    }

    public List<OrderDTO> getOrdersOfMember(String memberId,int pageNumber,int pageSize,String orderBy,String orderField){
        // check member exists
        Map<String,Object> memberCheckResult= memberInterface.checkIfMemberExists(memberId);
//        String url = memberURLMapper.getMemberExistsURL(memberId);
//        Map<String,Object> memberCheckResult= restTemplate.getForObject(url,Map.class);
        if(Utility.isNullOrEmpty(memberCheckResult) || !Boolean.parseBoolean(String.valueOf(memberCheckResult.get("is_present")))){
            throw new CustomException(Utility.buildErrorObject("MEMBER_NOT_FOUND","Member doesn't exists with provided member id",404,"getOrdersOfMember"));
        }
        Sort sort="ASC".equals(orderBy)?Sort.by(orderField).ascending():Sort.by(orderField).descending();
        PageRequest pageRequest=PageRequest.of(pageNumber,pageSize,sort);
        Page<Order> memberOrders=orderRepository.findAllByMemberId(Long.valueOf(memberId),pageRequest);
        List<OrderDTO> orderDTOList=memberOrders.stream().map(order -> {
            OrderDTO orderDTO=ModelMapperUtility.map(order,OrderDTO.class);
            List<OrderItemDTO> orderItemDTOList=order.getOrderItems().parallelStream().map(orderItem -> ModelMapperUtility.map(orderItem,OrderItemDTO.class)).toList();
            orderDTO.setOrderItemDTOList(orderItemDTOList);
            return orderDTO;
        }).toList();
        log.info(orderDTOList.size()+" Orders Fetched");
        return orderDTOList;
    }

    public Map<String,Object> orderExistence(String orderId){
        if(!Utility.isNumeric(orderId))
            throw new CustomException(Utility.buildErrorObject("INVALID_ORDER_ID","Order Id Provided is Not Valid",400,"orderExistence"));
        Map<String,Object> orderExists=new HashMap<>();
        orderExists.put("order_id",orderId);
        orderExists.put("is_present",orderRepository.existsById(Long.valueOf(orderId)));
        return orderExists;
    }

    public OrderDTO updateOrder(OrderDTO request,String orderId){
        try{
            if(Utility.isNullOrEmpty(request))
                throw new CustomException(Utility.buildErrorObject(CommonErrors.REQUEST_BODY_MISSING.toString(),CommonErrors.REQUEST_BODY_MISSING.getMessage(), 400,"updateOrder"));
            if(!Utility.isNumeric(orderId))
                throw new CustomException(Utility.buildErrorObject("INVALID_ORDER_ID","Order Id Provided is Not Valid",400,"updateOrder"));
            if(!Utility.isNullOrEmpty(request.getDeliveryPartnerId())) {
//            Map<String,Object> deliveryPartnerCheckResult= deliveryPartnerInterface.isDeliveryPartnerExists(request.getDeliveryPartnerId().toString());
                String url = deliveryPartnerURLMapper.getDeliveryPartnerExistsURL(request.getDeliveryPartnerId().toString());
                Map<String, Object> deliveryPartnerCheckResult = restTemplate.getForObject(url, Map.class);
                if (Utility.isNullOrEmpty(deliveryPartnerCheckResult) || !(boolean) deliveryPartnerCheckResult.get("is_present"))
                    throw new CustomException(Utility.buildErrorObject("DELIVERY_PARTNER_NOT_FOUND", "Delivery Partner doesn't exists with provided partner id", 404, "updateOrder"));
            }
            Order order=orderRepository.findById(Long.valueOf(orderId)).orElseThrow(()->new EntityNotFoundException("Order Not Found"));
            setUpdatedOrderDetails(order,request);
            Order savedOrder=orderRepository.save(order);
            return ModelMapperUtility.map(savedOrder,OrderDTO.class);
        } catch (JsonProcessingException e){
            throw new CustomException(Utility.buildErrorObject("PARSING_ERROR", "Error while parsing the request", 500, "updateOrder"));
        }

    }

    private void setUpdatedOrderDetails(Order order,OrderDTO request) throws JsonProcessingException {
        OrderDTO currentOrderDetails=ModelMapperUtility.map(order, OrderDTO.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        String orderRequestJson=objectMapper.writeValueAsString(request);
        objectMapper.readerForUpdating(currentOrderDetails).readValue(orderRequestJson);
        BeanUtils.copyProperties(currentOrderDetails,order);
    }

    private RestaurantDTO getRestaurantDetails(String restaurantId){
//        RestaurantDTO restaurantDTO=restaurantInterface.getRestaurant(restaurantId);
        String restaurantUrl = restaurantURLMapper.getRestaurantExistsURL(String.valueOf(restaurantId));
        RestaurantDTO restaurantResult= restTemplate.getForObject(restaurantUrl,RestaurantDTO.class);
        if(Utility.isNullOrEmpty(restaurantResult))
            throw new CustomException(Utility.buildErrorObject("RESTAURANT_NOT_FOUND","Restaurant doesn't exists with provided restaurant id",404,"getRestaurantDetails"));
        return restaurantResult;
    }
}
