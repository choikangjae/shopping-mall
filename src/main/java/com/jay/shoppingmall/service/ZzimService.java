package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ZzimService {

    private final ZzimRepository zzimRepository;

    public boolean isZzimed(User user, Long itemId) {
        if (user == null) {
            return false;
        }
        boolean isZzimed = false;

        Zzim zzim = zzimRepository.findByUserIdAndItemId(user.getId(), itemId);

        if (zzim != null) {
            isZzimed = zzim.getIsZzimed();
        }
        return isZzimed;
    }
}
