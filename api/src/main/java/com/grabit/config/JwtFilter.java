package com.grabit.config;

import com.grabit.Utilities.JWTUtil;
import com.grabit.Utilities.Utility;
import com.grabit.bean.auth.PermissionDTO;
import com.grabit.bean.auth.RoleDTO;
import com.grabit.exception.CustomException;
import com.grabit.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Log4j2
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    RestTemplate restTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = Utility.isNullOrEmpty(request.getHeader("Authorization"))?request.getHeader("Authorization"):request.getHeader("authorization");
            String token = null;
            String email = null;
            List<Long> permissionsList = null;
            Long roleId=null;
            if (authHeader != null && authHeader.startsWith("JWT ")) {
                token = authHeader.substring(4);
                Claims claims=JWTUtil.extractClaims(token);
                email = JWTUtil.extractEmail(claims);
                permissionsList = JWTUtil.extractPermissions(claims);
                roleId=Long.valueOf(JWTUtil.extractRole(claims));
            }

            if (token != null && email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Set<SimpleGrantedAuthority> authorities = new HashSet<>();

                if (permissionsList == null || permissionsList.isEmpty()) {
                    throw new CustomException(Utility.buildErrorObject("PERMISSIONS_MISSING", "User do not have any valid permissions", 500, "jwtFilter"));
                }
                ResponseEntity<RoleDTO> roleAndPermissionsDetails = restTemplate.getForEntity(System.getenv("auth_url") + "/role/"+roleId+"/", RoleDTO.class);
                if (Utility.isNullOrEmpty(roleAndPermissionsDetails.getBody()) || Utility.isNullOrEmpty(roleAndPermissionsDetails.getBody().getRole()))
                    throw new CustomException(Utility.buildErrorObject("INVALID_RESPONSE", "response received from auth service while fetching role details is null or not valid", 500, "jwtFilter"));

                authorities.add(new SimpleGrantedAuthority("ROLE_"+roleAndPermissionsDetails.getBody().getRole().name()));
                for (PermissionDTO permissionDTO : roleAndPermissionsDetails.getBody().getPermissions()) {
                    if (permissionsList.contains(permissionDTO.getId())) {
                        authorities.add(new SimpleGrantedAuthority(permissionDTO.getPermission().name()));
                    }
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (JwtAuthenticationException e){
            log.info("Authentication not successful : "+ Utility.toJson(e.getAuthenticationError()));
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("responseWriterFlag",true);
            response.getWriter().write(Utility.toJson(e.getAuthenticationError()));
        }
    }
}
