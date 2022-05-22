package com.jay.shoppingmall.domain.payment;

import com.jay.shoppingmall.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
