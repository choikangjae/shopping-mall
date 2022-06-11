package com.jay.shoppingmall.domain.order.order_item;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.order.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
//    List<Item> findAllByOrderId(Long id);
//    List<OrderItem> findByItemId(Long itemId);

    List<OrderItem> findByOrderId(Long id);

    List<OrderItem> findBySellerId(Long sellerId, Pageable pageable);

    List<OrderItem> findByOrderIdAndSellerId(Long orderId, Long sellerId);
}

