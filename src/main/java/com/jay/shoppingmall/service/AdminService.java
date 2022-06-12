package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.dto.response.user.UserDetailResponse;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserDetailResponse> showUserList(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        List<UserDetailResponse> userDetailResponses = new ArrayList<>();
        for (User user : users) {

            userDetailResponses.add(UserDetailResponse.builder()
                    .email(user.getEmail())
                    .fullName(user.getName().getFullName())
                    .fullPhoneNumber(user.getPhoneNumber().getFullNumber())
                    .fullAddress(user.getAddress().getFullAddress())
                    .role(user.getRole())
                    .isMandatoryAgree(user.getAgree().getIsMandatoryAgree())
                    .isMarketingAgree(user.getAgree().getIsMarketingAgree())
                    .build());
        }
        return userDetailResponses;
    }
    public List<UserDetailResponse> searchUsersByEmail(String email) {
        List<User> users = userRepository.findByEmailContaining(email)
                .orElseThrow(() -> new UserNotFoundException("유저가 존재하지 않습니다"));

        List<UserDetailResponse> userDetailResponses = new ArrayList<>();
        for (User user : users) {

            userDetailResponses.add(UserDetailResponse.builder()
                    .email(user.getEmail())
                    .fullName(user.getName().getFullName())
                    .fullPhoneNumber(user.getPhoneNumber().getFullNumber())
                    .fullAddress(user.getAddress().getFullAddress())
                    .role(user.getRole())
                    .isMandatoryAgree(user.getAgree().getIsMandatoryAgree())
                    .isMarketingAgree(user.getAgree().getIsMarketingAgree())
                    .build());
        }
        return userDetailResponses;
    }
}
