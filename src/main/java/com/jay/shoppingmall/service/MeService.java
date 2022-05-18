package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.user.model.Agree;
import com.jay.shoppingmall.dto.AgreeRequest;
import com.jay.shoppingmall.dto.MeDetailResponse;
import com.jay.shoppingmall.exception.AgreeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;


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
            throw new AgreeException("Invalid Request");
        }
        if (!agreeRequest.getIsMandatoryAgree()) {
            throw new AgreeException("You have to agree the mandatory one");
        }

        Agree agree = Agree.builder()
                .isMandatoryAgree(agreeRequest.getIsMandatoryAgree())
                .isMarketingAgree(agreeRequest.getIsMarketingAgree())
                .build();

        user.setAgree(agree);

        return true;
    }
}
