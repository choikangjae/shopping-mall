package com.jay.shoppingmall.domain.payment;

import com.jay.shoppingmall.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByMerchantUid(String merchantUid);

//    Optional<Page<Payment>> findByUserId(Long id, Pageable pageable);
}
