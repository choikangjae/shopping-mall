package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.MeDetailResponse;
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
}
