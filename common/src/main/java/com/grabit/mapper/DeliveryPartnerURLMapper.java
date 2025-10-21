package com.grabit.mapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

@Value
public class DeliveryPartnerURLMapper {

    @Getter(AccessLevel.NONE)
    String domainURL;

    public DeliveryPartnerURLMapper(String url){
        this.domainURL=url;
    }

    public String getDeliveryPartnerExistsURL(String deliveryPartnerId){
        return domainURL+"/delivery-partner/"+deliveryPartnerId+"/exists";
    }
}
