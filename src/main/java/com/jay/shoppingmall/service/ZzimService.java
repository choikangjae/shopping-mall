package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ZzimService {

    private final ZzimRepository zzimRepository;

    @Transactional(readOnly = true)
    public boolean isZzimed(User user, Long itemId) {
        if (user == null) {
            return false;
        }
        Zzim zzim = zzimRepository.findByUserIdAndItemId(user.getId(), itemId);

        if (zzim != null) {
            return zzim.getIsZzimed();
        }
        return false;
    }
}
