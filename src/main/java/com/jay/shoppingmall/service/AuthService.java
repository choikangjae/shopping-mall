package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.dto.request.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signup(SignupRequest signupRequest) {

        String password = passwordEncoder.encode(signupRequest.getPassword());

//        User user = signupRequest.toEntity(password);
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(password)
//                .agree(new Agree(false, false))
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    public User sellerSignup(final SignupRequest signupRequest) {
        String password = passwordEncoder.encode(signupRequest.getPassword());

//        User user = signupRequest.toEntity(password);
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(password)
//                .agree(new Agree(false, false))
                .role(Role.ROLE_SELLER)
                .build();

        return userRepository.save(user);
    }
}
