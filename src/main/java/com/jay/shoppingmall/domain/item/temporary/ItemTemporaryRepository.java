package com.jay.shoppingmall.domain.item.temporary;

import com.jay.shoppingmall.domain.item.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemTemporaryRepository extends JpaRepository<ItemTemporary, Long> {
    List<ItemTemporary> findAllBySellerId(Long id);
}
