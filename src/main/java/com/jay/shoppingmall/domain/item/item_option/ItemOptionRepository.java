package com.jay.shoppingmall.domain.item.item_option;

import com.jay.shoppingmall.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {

    List<ItemOption> findAllByItemId(Long itemId);

    Optional<ItemOption> findByOption1AndOption2AndItemId(String option1, String option2, Long id);

    ItemOption findByItemIdAndIsOptionMainItemTrue(Long itemId);

    ItemOption findByItemPriceId(Long itemPriceId);

    List<ItemOption> findByIdIn(List<Long> itemOptionIds);
}
