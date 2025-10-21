package com.grabit.mapper;

import com.grabit.Utilities.Utility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

@Value
public class RestaurantURLMapper {
    @Getter(AccessLevel.NONE)
    String domainUrl;

    public RestaurantURLMapper(String url){
        this.domainUrl=url;
    }

    public String getRestaurantExistsURL(String restaurantId){
        return domainUrl+"/restaurant/"+restaurantId+"/exists";
    }

    public String getFoodItemExistsURL(String itemId){
        return domainUrl+"/items/"+itemId+"/exists";
    }

    public String getFoodItemExistsInABranchURL(Long itemId,Long branchId,String itemDetailsParams){
        if(Utility.isNullOrEmpty(itemDetailsParams))
            return domainUrl+"/items/"+itemId+"/branch/"+branchId+"/exists";
        return domainUrl+"/items/"+itemId+"/branch/"+branchId+"/exists?item_details="+itemDetailsParams;
    }

    public String getRestaurantAndBranchDetailsExistsURL(String restaurantId,String branchId){
        return domainUrl+"/restaurant/"+restaurantId+"/branch/"+branchId+"/exists";
    }

    public String getRestaurantAndBranchDetailsURL(Long restaurantId,Long branchId){
        return domainUrl+"/restaurant/"+restaurantId+"/branch/"+branchId;
    }

    public String getRestaurantAndBranchNamesURL(Long restaurantId,Long branchId){
        return domainUrl+"/restaurant/"+restaurantId+"/branch/"+branchId+"/name";
    }

    public String getRestaurantAndBranchOrdersCountUpdateURL(Long restaurantId,Long branchId){
        return domainUrl+"/restaurant/"+restaurantId+"/branch/"+branchId+"/orders/update";
    }

    public String getUpdateFoodItemOrdersAndAvailabilityURL(){
        return domainUrl+"/items/ordered/update";
    }
}
