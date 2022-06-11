package com.jay.shoppingmall.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndOrderItemId(Long userId, Long orderItemId);
}
