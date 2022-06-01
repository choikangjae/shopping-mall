package com.jay.shoppingmall.controller.common;

import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.UserValidationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(final Class<?> clazz) {
        return UserValidationRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        UserValidationRequest userValidationRequest = (UserValidationRequest) target;

        if (!ObjectUtils.isEmpty(userValidationRequest.getEmail()) && userRepository.findByEmail(userValidationRequest.getEmail()).isPresent()) {
            errors.rejectValue("email", "DUPLICATED_EMAIL", "이미 해당 이메일로 가입이 되어있습니다.");
        }

        if (!userValidationRequest.getPassword().equals(userValidationRequest.getRepeatPassword())) {
            errors.rejectValue("password", "PASSWORD_NOT_MATCH", "비밀번호가 같지 않습니다.");
            errors.rejectValue("repeatPassword", "PASSWORD_NOT_MATCH", "비밀번호가 같지 않습니다.");
        }
    }
}
