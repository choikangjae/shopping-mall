package com.jay.shoppingmall.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByImageRelationAndId(ImageRelation imageRelation, Long id);

    Image findByImageRelationAndForeignId(ImageRelation imageRelation, Long foreignId);

    List<Image> findAllByImageRelationAndForeignId(ImageRelation imageRelation, Long foreignId);

    List<Image> findAllByForeignId(Long foreignId);
}
