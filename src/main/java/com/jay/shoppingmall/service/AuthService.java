package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.SignupRequest;
import com.jay.shoppingmall.exception.PasswordInvalidException;
import com.jay.shoppingmall.exception.UserDuplicatedException;
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

    public void signup(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new UserDuplicatedException("User Duplicated");
        }

        if (!signupRequest.getPassword().equals(signupRequest.getRepeatPassword())) {
            throw new PasswordInvalidException("Password does not match");
        }

        String password = passwordEncoder.encode(signupRequest.getPassword());

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(password)
                .build();

        userRepository.save(user);
    }
}
