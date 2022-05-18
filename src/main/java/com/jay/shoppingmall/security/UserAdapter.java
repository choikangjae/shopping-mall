package com.jay.shoppingmall.security;

import com.jay.shoppingmall.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;


@Getter
public class UserAdapter extends org.springframework.security.core.userdetails.User {
    private User user;

    public UserAdapter(User user) {
        super(user.getEmail(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole().toString())));
        this.user = user;
    }
}
