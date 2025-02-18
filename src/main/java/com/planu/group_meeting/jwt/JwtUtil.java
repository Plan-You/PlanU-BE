package com.planu.group_meeting.jwt;

import com.planu.group_meeting.exception.user.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.planu.group_meeting.jwt.JwtFilter.EXPIRED_JWT_TOKEN_MESSAGE;
import static com.planu.group_meeting.jwt.JwtFilter.INVALID_JWT_TOKEN_MESSAGE;

@Component
public class JwtUtil {
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;  // 7일
    public static final String REFRESH_TOKEN_PREFIX = "refresh: ";
    private final RedisTemplate<String, String> redisTemplate;
    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret, RedisTemplate<String, String> redisTemplate) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.redisTemplate = redisTemplate;
    }

    public String getCategory(String token){
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateToken(String token){
        try{
            parseToken(token);
        } catch (ExpiredJwtException e){
            throw new InvalidTokenException(EXPIRED_JWT_TOKEN_MESSAGE);
        } catch(SignatureException | MalformedJwtException e ){
            throw new InvalidTokenException(INVALID_JWT_TOKEN_MESSAGE);
        }
    }

    public String createAccessToken(String username, String role) {

        return Jwts.builder()
                .claim("category","access")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken(String username, String role) {
        String refresh = Jwts.builder()
                .claim("category","refresh")
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(secretKey)
                .compact();

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + username,
                refresh,
                REFRESH_TOKEN_EXPIRE_TIME,
                TimeUnit.MILLISECONDS
        );
        return refresh;
    }

}
