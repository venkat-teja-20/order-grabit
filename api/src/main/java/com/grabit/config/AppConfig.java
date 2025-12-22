package com.grabit.config;

import com.grabit.mapper.DeliveryPartnerURLMapper;
import com.grabit.mapper.MemberURLMapper;
import com.grabit.mapper.RestaurantURLMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AppConfig {

    @Bean
    public RestaurantURLMapper getRestaurantURLMapper(){
        return new RestaurantURLMapper(System.getenv("restaurant_url"));
    }

    @Bean
    public MemberURLMapper getMemberURLMapper(){
        return new MemberURLMapper(System.getenv("member_url"));
    }

    @Bean
    public DeliveryPartnerURLMapper getDeliveryPartnerURLMapper(){
        return new DeliveryPartnerURLMapper(System.getenv("delivery_partner_url"));
    }

    @Bean
    public NewTopic kafkaTopic(){
        return TopicBuilder
                .name("")
                .build();
    }
}
