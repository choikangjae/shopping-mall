package com.jay.shoppingmall.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

//    Order findByPaymentId(Long id);
    Optional<Page<Order>> findByUserId(Long id, Pageable pageable);

    List<Order> findByUserId(Long id);
}
