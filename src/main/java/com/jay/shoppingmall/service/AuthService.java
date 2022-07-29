package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.token.password.PasswordResetTokenRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.token.password.PasswordResetToken;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.dto.request.password.PasswordChangeRequest;
import com.jay.shoppingmall.dto.request.password.PasswordResetRequest;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import com.jay.shoppingmall.exception.exceptions.PasswordInvalidException;
import com.jay.shoppingmall.exception.exceptions.TokenExpiredException;
import com.jay.shoppingmall.exception.exceptions.UserDuplicatedException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public void userRegistration(UserValidationRequest userValidationRequest) {
        String encryptedPassword = passwordEncoder.encode(userValidationRequest.getPassword());

        User user = User.builder()
                .email(userValidationRequest.getEmail())
                .password(encryptedPassword)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
        log.info("A user has been registered. email = '{}'", user.getEmail());
    }

    public void sellerSignup(final UserValidationRequest userValidationRequest) {
        String password = passwordEncoder.encode(userValidationRequest.getPassword());

        Agree agree = Agree.builder()
                .isMandatoryAgree(true)
                .isMarketingAgree(false)
                .build();

        User user = User.builder()
                .email(userValidationRequest.getEmail())
                .password(password)
                .role(Role.ROLE_SELLER)
                .agree(agree)
                .build();
        userRepository.save(user);

        Seller seller = Seller.builder()
                .isLawAgree(true)
                .isSellerAgree(true)
                .isActivated(true)
                .userId(user.getId())
                .build();
        sellerRepository.save(seller);

        log.info("A seller has been registered. email = '{}'", user.getEmail());
    }
    public void passwordTokenSender(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByEmail(passwordResetRequest.getEmail())
                .orElseThrow(()-> new UserNotFoundException("해당 이메일이 존재하지 않습니다"));

        String token = UUID.randomUUID().toString();

        mailService.sendMail(user.getEmail(), token);
        log.info("PasswordResetToken email sent. email = '{}'", user.getEmail());

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .email(user.getEmail())
                .isExpired(false)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);
    }
    public void passwordTokenValidator(PasswordResetRequest passwordResetRequest) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByEmailAndToken(passwordResetRequest.getEmail(), passwordResetRequest.getToken())
                .orElseThrow(() -> {
                    log.info("Invalid passwordResetToken tried. email = '{}', token = '{}'", passwordResetRequest.getEmail(), passwordResetRequest.getToken());
                    return new TokenExpiredException("유효하지 않은 주소입니다. 다시 시도해주세요");
                });

        if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationTime())) {
            passwordResetToken.setIsExpired(true);
            log.info("PasswordResetToken has been expired. email = '{}', token = '{}'", passwordResetRequest.getEmail(), passwordResetRequest.getToken());
            throw new TokenExpiredException("주소가 만료되었습니다");
        }
    }
    public void passwordUpdateAfterReset(UserValidationRequest userValidationRequest) {
        User user = userRepository.findByEmail(userValidationRequest.getEmail())
                .orElseThrow(() -> {
                    log.info("Invalid email tried for user. email = '{}'", userValidationRequest.getEmail());
                    return new UserNotFoundException("유효하지 않은 이메일입니다");
                });
        String encryptedPassword = passwordEncoder.encode(userValidationRequest.getPassword());

        user.updatePassword(encryptedPassword);
    }

    public void passwordChange(final PasswordChangeRequest passwordChangeRequest, User user) {
        User updatedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> {
                    log.info("Invalid id tried for user. id = '{}'", user.getId());
                    return new UserNotFoundException("유효하지 않은 접근입니다");
                });

        if (!passwordEncoder.matches(passwordChangeRequest.getPasswordNow(), user.getPassword())) {
            log.info("Expected password not matches with actual current password. email = '{}'", user.getEmail());
            throw new PasswordInvalidException("현재 비밀번호가 일치하지 않습니다");
        }
        if (passwordEncoder.matches(passwordChangeRequest.getPasswordAfter(), user.getPassword())) {
            log.info("Current password is exactly equal to the password to be changed. email = '{}'", user.getEmail());
            throw new PasswordInvalidException("현재 비밀번호와 비꿀 비밀번호가 같습니다");
        }
        log.info("User changed password. email = '{}'", user.getEmail());
        updatedUser.updatePassword(passwordEncoder.encode(passwordChangeRequest.getPasswordAfter()));
    }
}
