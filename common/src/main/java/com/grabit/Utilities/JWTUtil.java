package com.grabit.Utilities;

import com.grabit.config.KeyProvider;
import com.grabit.enums.CommonErrors;
import com.grabit.exception.APIError;
import com.grabit.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.List;

@Component
@Log4j2
public class JWTUtil {

    private static PublicKey publicKey;

    public JWTUtil(KeyProvider keyProvider){
        publicKey=keyProvider.getPublicKey();
    }

    public static String extractEmail(Claims claims){
        return claims.getSubject();
    }

    public static String extractRole(Claims claims){
        return String.valueOf(claims.get("role"));
    }

    public static List extractPermissions(Claims claims) {
        return ((List) claims.get("permissions")).stream().map(permissionId->Long.valueOf(String.valueOf(permissionId))).toList();
    }

    public static Object extractUserId(String token) {
        return extractClaims(token).get("user_id");
    }

    public static Claims extractClaims(String token) {
        try {
            Claims claims=Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if("refresh_token".equals(String.valueOf(claims.get("access_level")))){
                throw new JwtAuthenticationException(new APIError("AUTHENTICATION_FAILED","Refresh Token cannot be used as Authentication Token"));
            }
            if(Utility.isNullOrEmpty(claims.get("role")) || Utility.isNullOrEmpty("user_id") || Utility.isNullOrEmpty("permissions")){
                throw new JwtAuthenticationException(new APIError("AUTHENTICATION_FAILED","The token provided is not valid"));
            }
            return claims;
        }
        catch (SignatureException | MalformedJwtException | IllegalArgumentException | UnsupportedJwtException e){
            log.info("extractClaims -> "+e);
            throw new JwtAuthenticationException(new APIError(CommonErrors.AUTHENTICATION_FAILED.toString(),CommonErrors.AUTHENTICATION_FAILED.getMessage()));
        }
        catch (ExpiredJwtException e){
            log.info("extractClaims -> "+e);
            throw new JwtAuthenticationException(new APIError(CommonErrors.AUTHENTICATION_EXPIRED.toString(),CommonErrors.AUTHENTICATION_EXPIRED.getMessage()));
        } catch (Exception e){
            if(e instanceof JwtAuthenticationException jwtAuthenticationException)
                throw jwtAuthenticationException;
            log.info("extractClaims -> "+e);
            throw new JwtAuthenticationException(new APIError(CommonErrors.AUTHENTICATION_FAILED.toString(),CommonErrors.AUTHENTICATION_FAILED.getMessage()));
        }
    }
}
