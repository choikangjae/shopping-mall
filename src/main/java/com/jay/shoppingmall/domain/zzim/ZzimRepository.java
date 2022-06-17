package com.jay.shoppingmall.domain.zzim;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZzimRepository extends JpaRepository<Zzim, Long> {
    Page<Zzim> findByUser(User user, Pageable pageable);

    Zzim findByUserIdAndItemId(Long userId, Long itemId);

    void deleteByItemId(Long id);
}
