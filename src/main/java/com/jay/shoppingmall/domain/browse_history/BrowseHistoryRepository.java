package com.jay.shoppingmall.domain.browse_history;

import com.jay.shoppingmall.domain.cart.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BrowseHistoryRepository extends JpaRepository<BrowseHistory, Long> {

    BrowseHistory findByItemId(Long itemId);

    List<BrowseHistory> findAllByUserIdOrderByBrowseAtDesc(Long userId);

    Page<BrowseHistory> findAllByUserIdOrderByBrowseAtDesc(Long userId, Pageable pageable);

    BrowseHistory findFirstByUserIdOrderByBrowseAtDesc(Long userId);
}
