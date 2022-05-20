package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.dto.ItemResponse;
import com.jay.shoppingmall.dto.ItemDetailResponse;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final FileHandler fileHandler;

    public List<ItemResponse> itemAll() {
        List<ItemResponse> responses = new ArrayList<>();

         List<Item> items = itemRepository.findAll();
         for (Item item : items) {
             Image image = imageRepository.findByItemId(item.getId());

             if (image == null) {
                 continue;
             }
             String stringImage = fileHandler.getStringImage(image);

             ItemResponse itemResponse = new ItemResponse(
                     item.getId(), item.getName(), item.getPrice(), stringImage);

             responses.add(itemResponse);
         }
         return responses;
    }


    public ItemDetailResponse itemDetail(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                ()-> new ItemNotFoundException("No Items Found"));

        Image image = item.getImageList().get(0);
        String stringImage = fileHandler.getStringImage(image);

        return ItemDetailResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stock(item.getStock())
                .image(stringImage)
                .build();
    }
}
