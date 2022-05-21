package com.jay.shoppingmall.controller.common;

import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.SignupRequest;
import com.jay.shoppingmall.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UpdateValidator implements Validator {

    private final UserRepository userRepository;

    @Override
    public boolean supports(final Class<?> clazz) {
        return UserUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        UserUpdateRequest userUpdateRequest = (UserUpdateRequest) target;
        String compactedPhoneNumber = "";

        //TODO 핸드폰 번호 형식 맞추기
        if (userUpdateRequest.getPhoneNumber().contains("-")) {
            compactedPhoneNumber = userUpdateRequest.getPhoneNumber().replace("-","");
        }
        //TODO 이름이 영어면 전부 영어, 한글이면 전부 한글, 숫자는 못 들어오게.
        if (userUpdateRequest.getLastName().contains("e")) {

        }

//
//        if (!signupRequest.getPassword().equals(signupRequest.getRepeatPassword())) {
//            errors.rejectValue("password", "PasswordNotMatch", "비밀번호가 같지 않습니다.");
//            errors.rejectValue("repeatPassword", "PasswordNotMatch", "비밀번호가 같지 않습니다.");
//        }
    }
}
