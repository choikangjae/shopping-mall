package com.jay.shoppingmall.domain.cart;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<List<Cart>> findByUser(User user);

    Optional<List<Cart>> findByUserAndIsSelectedTrue(User user);
//    Cart findByUserAndItem(User user, Item item);
//
//    Optional<Cart> findByUserIdAndItemIdAndItemOptionId(Long userId, Long itemId, Long itemOptionId);

    Optional<Cart> findByUserAndItemAndItemOption(User user, Item item, ItemOption itemOption);

    void deleteByItemId(Long id);
}
