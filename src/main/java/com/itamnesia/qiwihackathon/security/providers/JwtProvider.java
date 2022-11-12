package com.itamnesia.qiwihackathon.security.providers;

import com.itamnesia.qiwihackathon.security.JwtAuthenticationException;
import com.itamnesia.qiwihackathon.security.token.AccessTokenService;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final AccessTokenService accessTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            if (authentication == null) {
                throw new JwtAuthenticationException();
            }
            var accessToken = authentication.getCredentials().toString();
            if (!accessTokenService.isValid(accessToken)) {
                throw new JwtAuthenticationException();
            }
            var login = accessTokenService.getPhoneNumber(accessToken);
            var role = accessTokenService.getRole(accessToken);
            var principal = userDetailsService.loadUserByUsername(login);
            var hasAuthority = principal.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals(role));
            if (hasAuthority) {
                return new UsernamePasswordAuthenticationToken(principal, login, principal.getAuthorities());
            }
            throw new JwtAuthenticationException();
        } catch (JwtException exception) {
            throw new JwtAuthenticationException();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
