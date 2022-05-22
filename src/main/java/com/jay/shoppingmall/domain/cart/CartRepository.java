package com.jay.shoppingmall.domain.cart;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<List<Cart>> findByUser(User user);

    Cart findByUserAndItem(User user, Item item);

    void deleteByItemId(Long id);
}
