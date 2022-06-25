package com.jay.shoppingmall.domain.zzim;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ZzimRepository extends JpaRepository<Zzim, Long> {

    List<Zzim> findByItemIdIn(List<Long> itemIds);

    Page<Zzim> findByUser(User user, Pageable pageable);

    Zzim findByUserIdAndItemId(Long userId, Long itemId);

    int deleteByUserIdAndItemId(Long userId, Long itemId);

    void deleteByItemId(Long id);
}
