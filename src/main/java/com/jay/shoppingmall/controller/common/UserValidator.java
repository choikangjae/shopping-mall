package com.jay.shoppingmall.controller.common;

import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import javax.naming.Binding;

@Component
@RequiredArgsConstructor
public class UserValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(final Class<?> clazz) {
        return SignupRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        SignupRequest signupRequest = (SignupRequest) target;

        if (!ObjectUtils.isEmpty(signupRequest.getEmail()) && userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            errors.rejectValue("email", "DuplicatedEmail", "이미 존재하는 이메일입니다.");
        }

        if (!signupRequest.getPassword().equals(signupRequest.getRepeatPassword())) {
            errors.rejectValue("password", "PasswordNotMatch", "비밀번호가 같지 않습니다.");
            errors.rejectValue("repeatPassword", "PasswordNotMatch", "비밀번호가 같지 않습니다.");

        }

    }
}
