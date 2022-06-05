package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Test
    void 키워드에_따라_결과가_나온다() {
        Item item1 = Item.builder()
                .name("상품명")
                .salePrice(1000L)
                .stock(20)
                .price(1500L)
                .description("상품설명")
                .build();
        Item item2 = Item.builder()
                .name("상세품상품명")
                .salePrice(1000L)
                .stock(20)
                .price(1500L)
                .description("상 품 설 명")
                .build();

//        itemRepository.save(item1);
//        itemRepository.save(item2);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

//        given(itemRepository.findByNameContaining(anyString())).willReturn(Optional.of(items));
//
//        String keyword = "상품";
//
//        List<ItemResponse> itemResponses = itemService.searchItemsByKeyword(keyword);

//        for (ItemResponse searchResponse : itemResponses) {
//            System.out.println(searchResponse.getName());
//        }
//        assertThat(itemResponses.size()).isEqualTo(2);
//        assertThat(itemResponses.get(0).getName().contains(keyword)).isTrue();
//        assertThat(itemResponses.get(1).getName().contains(keyword)).isTrue();

    }
}