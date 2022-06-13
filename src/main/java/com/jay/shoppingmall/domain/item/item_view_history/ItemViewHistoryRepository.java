package com.jay.shoppingmall.domain.item.item_view_history;

import com.jay.shoppingmall.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ItemViewHistoryRepository extends JpaRepository<ItemViewHistory, Long> {

    ItemViewHistory findByItemIdAndViewDateBetween(Long itemId, LocalDateTime startDate, LocalDateTime endDate);
}
