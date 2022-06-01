package com.jay.shoppingmall.controller.common;

import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.request.UserUpdateRequest;
import com.jay.shoppingmall.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import javax.validation.constraints.Pattern;
import java.util.Objects;

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
//        String compactedPhoneNumber = "";
//
//        //TODO 핸드폰 번호 형식 맞추기
//        if (userUpdateRequest.getPhoneNumber().contains("-")) {
//            compactedPhoneNumber = userUpdateRequest.getPhoneNumber().replace("-","");
//        }

        if (!userUpdateRequest.getPhoneNumber().matches("^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$")) {
            errors.rejectValue("phoneNumber", "INVALID_PHONE_NUMBER", "전화번호가 형식에 맞지않습니다");
        }

        if (!userUpdateRequest.getLastName().matches("^[가-힣]+$") && !userUpdateRequest.getFirstName().matches("^[가-힣]+$")) {
            if (!userUpdateRequest.getLastName().matches("^[a-zA-Z]+$") && !userUpdateRequest.getFirstName().matches("^[a-zA-Z]+$")) {
                errors.rejectValue("firstName", "INVALID_NAME", "성과 이름에는 완전한 한글이나 영어만 들어갈 수 있습니다");
            }
        }

        if (userUpdateRequest.getFirstName().matches("^[가-힣]+$")) {
            if (!userUpdateRequest.getLastName().matches("^[가-힣]+$")) {
                errors.rejectValue("lastName", "INVALID_NAME", "성이 한글이면 이름도 한글이어야 합니다");
            }
        }
        if (userUpdateRequest.getLastName().matches("^[가-힣]+$")) {
            if (!userUpdateRequest.getFirstName().matches("^[가-힣]+$")) {
                errors.rejectValue("firstName", "INVALID_NAME", "이름이 한글이면 성도 한글이어야 합니다");
            }
        }

        if (userUpdateRequest.getFirstName().matches("^[a-zA-Z]+$")) {
            if (!userUpdateRequest.getLastName().matches("^[a-zA-Z]+$")) {
                errors.rejectValue("lastName", "INVALID_NAME", "성이 영어면 이름도 영어여야 합니다");
            }
        }
        if (userUpdateRequest.getLastName().matches("^[a-zA-Z]+$")) {
            if (!userUpdateRequest.getFirstName().matches("^[a-zA-Z]+$")) {
                errors.rejectValue("firstName", "INVALID_NAME", "이름이 영어면 성도 영어여야 합니다");
            }
        }

    }
}
