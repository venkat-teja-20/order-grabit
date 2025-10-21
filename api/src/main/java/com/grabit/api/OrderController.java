package com.grabit.api;

import com.grabit.bean.order.OrderDTO;
import com.grabit.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping(value = "/orders")
public class OrderController {

    private static final String PAGE_NUMBER = "0";
    private static final String PAGE_SIZE = "10";
    private static final String ORDERING = "ASC";
    private static final String SORT_FIELD = "id";

    private static final String MEMBER_ORDERS_PAGE_SIZE="5";

    @Autowired
    OrderService orderService;

    @PostMapping(value = "/{memberId}/place",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public Object placeOrder(@RequestBody OrderDTO request, @PathVariable(value = "memberId") String memberId, HttpServletResponse response){
        response.setStatus(201);
        return orderService.placeOrder(request,memberId);
    }

    @GetMapping(value = "/restaurant/{restaurantId}/branch/{branchId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDTO> getOrders(@PathVariable(value = "restaurantId") String restaurantId, @PathVariable(value = "branchId") String branchId,
                                    @RequestParam(value = "page_number", required = false, defaultValue = PAGE_NUMBER) int pageNumber,
                                    @RequestParam(value = "page_size", required = false, defaultValue = PAGE_SIZE) int pageSize,
                                    @RequestParam(value = "order_by", required = false, defaultValue = ORDERING) String orderBy,
                                    @RequestParam(value = "field", required = false, defaultValue = SORT_FIELD) String orderField){
        return orderService.getAllOrders(restaurantId,branchId,pageNumber,pageSize,orderBy,orderField);
    }

    @GetMapping(value = "/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDTO getOrderById(@PathVariable(value = "id") String orderId){
        return orderService.getSingleOrder(orderId);
    }

    @GetMapping(value = "/members/{memberId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderDTO> getMemberOrders(@PathVariable(value = "memberId") String memberId,
                                 @RequestParam(value = "page_number", required = false, defaultValue = PAGE_NUMBER) int pageNumber,
                                 @RequestParam(value = "page_size", required = false, defaultValue = MEMBER_ORDERS_PAGE_SIZE) int pageSize,
                                 @RequestParam(value = "order_by", required = false, defaultValue = ORDERING) String orderBy,
                                 @RequestParam(value = "field", required = false, defaultValue = SORT_FIELD) String orderField){
        return orderService.getOrdersOfMember(memberId,pageNumber,pageSize,orderBy,orderField);
    }

    @GetMapping(value = "/{id}/exists",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> checkIfOrderExists(@PathVariable(value = "id") String orderId){
        return orderService.orderExistence(orderId);
    }

    @PatchMapping(value = "/{id}",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderDTO updateOrderDetails(@RequestBody OrderDTO orderDTO,@PathVariable(value = "id") String orderId){
        return orderService.updateOrder(orderDTO,orderId);
    }
}
