package com.jay.shoppingmall.domain.order.order_item.order_delivery;

import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {
}
