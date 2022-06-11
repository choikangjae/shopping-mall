package com.jay.shoppingmall.domain.point;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {

    Point findByUserId(Long userId);
}
