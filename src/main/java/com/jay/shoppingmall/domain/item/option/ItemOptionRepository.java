package com.jay.shoppingmall.domain.item.option;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {
    List<ItemOption> findByItemId(Long id);

    List<ItemOption> findAllByOption1(String option1);

//    List<String> findOption2ByOption1AndItemId(String option1, Long id);
}
