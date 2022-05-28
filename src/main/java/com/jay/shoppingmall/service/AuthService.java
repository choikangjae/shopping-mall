package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.token.password.PasswordResetTokenRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.token.password.PasswordResetToken;
import com.jay.shoppingmall.dto.request.PasswordResetRequest;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import com.jay.shoppingmall.exception.exceptions.TokenExpiredException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public User userRegistration(UserValidationRequest userValidationRequest) {
        String encryptedPassword = passwordEncoder.encode(userValidationRequest.getPassword());

        User user = User.builder()
                .email(userValidationRequest.getEmail())
                .password(encryptedPassword)
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    public User sellerSignup(final UserValidationRequest userValidationRequest) {
        String password = passwordEncoder.encode(userValidationRequest.getPassword());

        User user = User.builder()
                .email(userValidationRequest.getEmail())
                .password(password)
                .role(Role.ROLE_SELLER)
                .build();

        return userRepository.save(user);
    }
    public void passwordTokenSender(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(()-> new UserNotFoundException("해당 이메일이 존재하지 않습니다"));

        String token = UUID.randomUUID().toString();

        mailService.sendMail(user.getEmail(), token);

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .email(user.getEmail())
                .isExpired(false)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);
    }
    public void passwordTokenValidator(PasswordResetRequest passwordResetRequest) {

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByEmailAndToken(passwordResetRequest.getEmail(), passwordResetRequest.getToken())
                .orElseThrow(() -> new TokenExpiredException("유효하지 않은 주소입니다. 다시 시도해주세요"));

        if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationTime())) {
            passwordResetToken.setIsExpired(true);
            throw new TokenExpiredException("주소가 만료되었습니다");
        }
    }
    public void passwordUpdate(UserValidationRequest userValidationRequest) {
        User user = userRepository.findByEmail(userValidationRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("유효하지 않은 이메일입니다"));
        String encryptedPassword = passwordEncoder.encode(userValidationRequest.getPassword());

        user.updatePassword(encryptedPassword);
    }

}
