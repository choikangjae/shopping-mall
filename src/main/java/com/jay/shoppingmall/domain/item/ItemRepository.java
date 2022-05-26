package com.jay.shoppingmall.domain.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<List<Item>> findByNameContaining(@Param("q") String name);

    Optional<List<Item>> findByUserId(Long id);

//    Slice<Item> findAll(Pageable pageable);
}
