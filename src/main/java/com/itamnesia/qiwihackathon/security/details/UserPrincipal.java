package com.itamnesia.qiwihackathon.security.details;


import com.itamnesia.qiwihackathon.model.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public record UserPrincipal(
        long id,
        String email,
        String password,
        Collection<? extends GrantedAuthority> authorities)
        implements UserDetails {

    public UserPrincipal(User user) {
       this(
               user.getId(),
               user.getLogin(),
               user.getPassword(),
               Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
