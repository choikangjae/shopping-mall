package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.dto.ItemResponse;
import com.jay.shoppingmall.dto.ItemDetailResponse;
import com.jay.shoppingmall.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;

    public List<ItemResponse> itemAll() {
        List<ItemResponse> responses = new ArrayList<>();

         List<Item> items = itemRepository.findAll();
         for (Item item : items) {

             Image image = imageRepository.findByItemId(item.getId());

             if (image == null) {
                 continue;
             }

             String absolutePath = new File("").getAbsolutePath() + File.separator;
             String path = absolutePath + image.getFilePath();
             byte[] imageBytes;

             try {
                 InputStream inputStream = new FileInputStream(path);
                 imageBytes = IOUtils.toByteArray(inputStream);
                 String stringImage = Base64.getEncoder().encodeToString(imageBytes);

                 System.out.println(path);
                 System.out.println(inputStream);

                 ItemResponse itemResponse = new ItemResponse(
                         item.getId(), item.getName(), item.getPrice(), stringImage);

                 responses.add(itemResponse);
                 inputStream.close();

             } catch (Exception e) {
                 e.printStackTrace();
             }


         }
         return responses;
    }

    public ItemDetailResponse itemDetail(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                ()-> new ItemNotFoundException("No Items Found"));

        return ItemDetailResponse.builder()
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stock(item.getStock())
//                .image() 이미지 처리를 따로 만들어서.
                .build();
    }
}
