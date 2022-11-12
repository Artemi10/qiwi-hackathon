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
    @Value("${jwt.payment.expired}")
    private Long validPaymentTimePeriod;
    private final TimeService timeService;

    @Override
    public String createAccessPaymentToken(User user) {
        return createToken(user, validPaymentTimePeriod);
    }

    @Override
    public String createAccessToken(User user){
        return createToken(user, validTimePeriod);
    }


    private String createToken(User user, Long period) {
        var claims = Jwts.claims()
                .setSubject(user.getPhoneNumber());
        claims.put("role", user.getRole().name());
        var currentTime = timeService.now();
        var expirationTime = currentTime.plusSeconds(period);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(expirationTime.toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    @Override
    public String getPhoneNumber(String accessToken){
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
