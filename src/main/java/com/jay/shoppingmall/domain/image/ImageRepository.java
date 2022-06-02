package com.jay.shoppingmall.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

//    Image findByItemId(Long id);

    Image findByItemIdAndIsMainImageTrue(Long id);

    List<Image> findAllByItemId(Long id);
}
