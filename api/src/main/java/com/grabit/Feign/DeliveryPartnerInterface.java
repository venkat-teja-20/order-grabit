package com.grabit.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "deliverypartnerservice")
public interface DeliveryPartnerInterface {
    @GetMapping(value = "/{id}/exists",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> isDeliveryPartnerExists(@PathVariable(value = "id") String deliveryPartnerId);
}
