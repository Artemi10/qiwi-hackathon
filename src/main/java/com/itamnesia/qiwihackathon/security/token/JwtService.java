package com.itamnesia.qiwihackathon.security.token;

import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.service.time.TimeService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.FixedClock;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Setter
@RequiredArgsConstructor
public class JwtService implements AccessTokenService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expired}")
    private Long validTimePeriod;
    private final TimeService timeService;

    @Override
    public String createAccessToken(User user){
        var claims = Jwts.claims()
                .setSubject(user.getLogin());
        claims.put("role", user.getRole().name());
        var currentTime = timeService.now();
        var expirationTime = currentTime.plusSeconds(validTimePeriod);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(expirationTime.toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    @Override
    public String getLogin(String accessToken){
        try {
            return Jwts.parser()
                    .setClock(new FixedClock(Date.from(timeService.now().toInstant())))
                    .setSigningKey(secretKey)
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException exception) {
            return exception.getClaims().getSubject();
        }
    }

    @Override
    public String getRole(String accessToken) {
        try {
            return Jwts.parser()
                    .setClock(new FixedClock(Date.from(timeService.now().toInstant())))
                    .setSigningKey(secretKey)
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get("role", String.class);
        } catch (ExpiredJwtException exception) {
            return exception.getClaims().get("role", String.class);
        }
    }

    @Override
    public boolean isValid(String accessToken){
        try {
            var currentTime = timeService.now().toInstant();
            var expirationTime = Jwts.parser()
                    .setClock(new FixedClock(Date.from(timeService.now().toInstant())))
                    .setSigningKey(secretKey)
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getExpiration()
                    .toInstant();
            return !expirationTime.isBefore(currentTime);
        } catch(JwtException e){
            return false;
        }
    }

}
