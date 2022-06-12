package com.jay.shoppingmall.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommonService {

    public String anonymousName(String name) {
        StringBuilder stringBuilder = new StringBuilder(name);
        stringBuilder.delete(stringBuilder.length() / 2, stringBuilder.length());
        stringBuilder.append("*".repeat(stringBuilder.length() / 2 + 1));
        return stringBuilder.toString();
    }

}
