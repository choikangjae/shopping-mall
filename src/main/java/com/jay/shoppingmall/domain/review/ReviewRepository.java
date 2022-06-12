package com.jay.shoppingmall.domain.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndOrderItemId(Long userId, Long orderItemId);

    Page<Review> findByUserId(Long UserId, Pageable pageable);

    List<Review> findFirst10ByUserIdOrderByCreatedDateDesc(Long userId);

    Page<Review> findAllByItemIdOrderByCreatedDateDesc(Long itemId, Pageable pageable);
}
