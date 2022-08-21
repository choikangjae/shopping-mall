package com.jay.shoppingmall.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonService {

    public String anonymousName(String name) {
        StringBuilder stringBuilder = new StringBuilder(name);
        final int length = stringBuilder.length() / 2;

        stringBuilder.delete(length, stringBuilder.length());
        stringBuilder.append("*".repeat(length));
        return stringBuilder.toString();
    }

}
