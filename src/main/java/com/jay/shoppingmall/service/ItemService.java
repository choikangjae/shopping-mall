package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.ItemResponse;
import com.jay.shoppingmall.dto.response.ItemDetailResponse;
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
    private final ZzimRepository zzimRepository;

    public List<ItemResponse> itemAll() {
        List<ItemResponse> responses = new ArrayList<>();

        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            Image image = imageRepository.findByItemIdAndIsMainImageTrue(item.getId());

            if (image == null) {
                continue;
            }

            String stringImage = fileHandler.getStringImage(image);

            ItemResponse itemResponse = ItemResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .price(item.getPrice())
//                    .salePrice(item.getSalePrice())
                    .zzim(item.getZzim())
                    .image(stringImage)
                    .build();
            responses.add(itemResponse);
        }
        return responses;
    }


    public ItemDetailResponse itemDetail(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("No Items Found"));
        Image image = imageRepository.findByItemIdAndIsMainImageTrue(item.getId());

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

    public List<ItemResponse> searchItemsByKeyword(String keyword) {
        List<Item> items = itemRepository.findByNameContaining(keyword)
                .orElseThrow(() -> new ItemNotFoundException("해당 키워드에 맞는 상품이 없습니다"));

        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            itemResponses.add(ItemResponse.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .zzim(item.getZzim())
                    .image(fileHandler.getStringImage(imageRepository.findByItemIdAndIsMainImageTrue(item.getId())))
                    .price(item.getPrice())
                    .salePrice(item.getSalePrice())
                    .build());
        }
        return itemResponses;
    }

    //아이템은 단 하나의 찜값만 가지고 그걸 더하고 빼고 할 것.
    //아이템은 찜값이 하나고 유저가 여러명.
    public Integer itemZzim(final ItemZzimRequest request, final User user) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new ItemNotFoundException("해당 상품을 찾을 수 없습니다"));
        Zzim zzim;
        if (zzimRepository.findByUserAndItem(user, item) == null) {
            zzim = Zzim.builder()
                    .user(user)
                    .item(item)
                    .isZzimed(false)
                    .build();
            zzimRepository.saveAndFlush(zzim);
        } else {
            zzim = zzimRepository.findByUserAndItem(user, item);
        }

        if (!zzim.getIsZzimed() || zzim.getIsZzimed() == null) {
            zzim.setIsZzimed(true);
            item.setZzim(item.getZzim() == null ? 1 : item.getZzim() + 1);
        } else {
            zzim.setIsZzimed(false);
            if (item.getZzim() == 0) {
                item.setZzim(0);
            } else {
                item.setZzim(item.getZzim() == null ? 0 : item.getZzim() - 1);
            }
        }

        return item.getZzim();
    }
}