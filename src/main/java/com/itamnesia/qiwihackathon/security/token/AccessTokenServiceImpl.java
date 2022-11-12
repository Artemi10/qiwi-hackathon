package com.itamnesia.qiwihackathon.security.token;

import com.itamnesia.qiwihackathon.model.user.User;
import com.itamnesia.qiwihackathon.service.time.TimeService;
import com.itamnesia.qiwihackathon.service.token.TokenGenerator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.FixedClock;
import lombok.RequiredArgsConstructor;

import javax.annotation.PostConstruct;
import java.util.Date;

@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {
    private String secretKey;
    private final Long validTimePeriod;
    private final TimeService timeService;
    private final TokenGenerator tokenGenerator;

    @PostConstruct
    public void generateKeys() {
        this.secretKey = tokenGenerator.generateToken();
    }

    @Override
    public String createToken(User user) {
        var claims = Jwts.claims()
                .setSubject(user.getPhoneNumber());
        claims.put("role", user.getRole());
        var currentTime = timeService.now();
        var expirationTime = currentTime.plusSeconds(validTimePeriod);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(currentTime.toInstant()))
                .setExpiration(Date.from(expirationTime.toInstant()))
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    @Override
    public String getPhoneNumber(String accessToken) {
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
    public boolean isValid(String token){
        try {
            var currentTime = timeService.now().toInstant();
            var expirationTime = Jwts.parser()
                    .setClock(new FixedClock(Date.from(timeService.now().toInstant())))
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .toInstant();
            return !expirationTime.isBefore(currentTime);
        } catch(JwtException e){
            return false;
        }
    }
}
