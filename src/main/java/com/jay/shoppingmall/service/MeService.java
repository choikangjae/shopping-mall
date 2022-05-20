package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.AgreeRequest;
import com.jay.shoppingmall.dto.MeDetailResponse;
import com.jay.shoppingmall.exception.exceptions.AgreeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean passwordCheck(String password, User user) {

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }
        return true;
    }

    public MeDetailResponse findById(final Long id) {
        User user = userRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));

        return MeDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    public boolean agreeCheck(final AgreeRequest agreeRequest, final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("No User Found"));
        if (user.getAgree().getIsMandatoryAgree()) {
            throw new AgreeException("잘못된 요청입니다");
        }
        if (!agreeRequest.getIsMandatoryAgree()) {
            throw new AgreeException("필수 항목을 반드시 동의하셔야 합니다");
        }

//        Agree agree = Agree.builder()
//                .isMandatoryAgree(agreeRequest.getIsMandatoryAgree())
//                .isMarketingAgree(agreeRequest.getIsMarketingAgree())
//                .build();
//
//        user.setAgree(agree);

        return true;
    }
}
