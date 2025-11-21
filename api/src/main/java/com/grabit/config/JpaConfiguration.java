package com.grabit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Configuration
public class JpaConfiguration {

    @Bean
    public AuditorAware<String> auditorAware(){
        return ()-> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication==null || !authentication.isAuthenticated())
                return Optional.of("Anonymous User");
            Object principal=authentication.getPrincipal();
            if(principal instanceof UserDetails userDetails)
                return Optional.ofNullable(userDetails.getUsername());
            else if(principal instanceof String)
                return Optional.of(String.valueOf(principal));
            return Optional.of("Anonymous User");
        };
    }
}
