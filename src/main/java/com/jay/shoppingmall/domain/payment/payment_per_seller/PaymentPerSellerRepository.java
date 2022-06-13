package com.jay.shoppingmall.domain.payment.payment_per_seller;

import com.jay.shoppingmall.domain.payment.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentPerSellerRepository extends JpaRepository<PaymentPerSeller, Long> {

    List<PaymentPerSeller> findByPayment(Payment payment);

    List<PaymentPerSeller> findBySellerIdOrderByCreatedDateDesc(Long sellerId, Pageable pageable);

    PaymentPerSeller findByOrderIdAndSellerId(Long orderId, Long sellerId);

    List<PaymentPerSeller> findBySellerIdAndCreatedDateBetween(Long sellerId, LocalDateTime startDate, LocalDateTime endDate);
}
