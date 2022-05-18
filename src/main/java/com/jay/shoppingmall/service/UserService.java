package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
//import com.jay.shoppingmall.security.CustomUserDetails;
import com.jay.shoppingmall.security.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentlyLoggedInUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        User user = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserAdapter) {
            user = ((UserAdapter) principal).getUser();
        }

        return user;
    }
}
