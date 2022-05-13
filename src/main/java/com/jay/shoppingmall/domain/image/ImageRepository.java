package com.jay.shoppingmall.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Image findByItemId(Long id);
}
