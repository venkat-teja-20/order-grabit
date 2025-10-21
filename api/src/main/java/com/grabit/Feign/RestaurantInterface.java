package com.grabit.Feign;

import com.grabit.bean.restaurant.FoodItemDTO;
import com.grabit.bean.restaurant.RestaurantDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "restaurantservice")
public interface RestaurantInterface {
    @GetMapping(value = "/restaurant/{id}/exists",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> checkRestaurantExists(@PathVariable(value = "id") String restaurantId);

    @GetMapping(value = "/items/{id}/exists",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> checkFoodItemExists(@PathVariable(value = "id") String idemId);

    @GetMapping(value = "/items/{id}/branch/{branchId}/exists",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> checkFoodItemExistsInABranch(@PathVariable(value = "id") String itemId, @PathVariable(value = "branchId") String branchId);

    @GetMapping(value = "/restaurant/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public RestaurantDTO getRestaurant(@PathVariable(value = "id") String restaurantId);

    @GetMapping(value = "/restaurant/{id}/branch/{branchId}/exists",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> checkRestaurantAndBranchExists(@PathVariable(value = "id") String restaurantId, @PathVariable(value = "branchId") String branchId);

    @PatchMapping(value = "/restaurant/{id}/branch/{branchId}/orders/update",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> updateRestaurantAndBranchOrders(@PathVariable(value = "id") String restaurantId, @PathVariable(value = "branchId") String branchId);

    @PatchMapping(value = "/items/ordered/update",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> updateFoodItemOrdersAndAvailability(@RequestBody List<FoodItemDTO> request);

    @GetMapping(value = "/restaurant/{id}/branch/{branchId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public RestaurantDTO getRestaurantAndBranch(@PathVariable(value = "id") String restaurantId, @PathVariable(value = "branchId") String branchId);

    @GetMapping(value = "/{id}/branch/{branchId}/name",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,String> getRestaurantAndBranchName(@PathVariable(value = "id") String restaurantId, @PathVariable(value = "branchId") String branchId);
}
