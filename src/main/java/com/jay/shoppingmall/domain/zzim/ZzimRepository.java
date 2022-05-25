package com.jay.shoppingmall.domain.zzim;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ZzimRepository extends JpaRepository<Zzim, Long> {
    Optional<List<Zzim>> findByUser(User user);

    Zzim findByUserAndItem(User user, Item item);

    void deleteByItemId(Long id);
}
