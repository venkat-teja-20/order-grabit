package com.grabit.Feign;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "memberservice")
public interface MemberInterface {
    @GetMapping(value = "/member/{memberId}/exists",produces = "application/json")
    Map<String, Object> checkIfMemberExists(@PathVariable(value = "memberId") String memberId);
}
