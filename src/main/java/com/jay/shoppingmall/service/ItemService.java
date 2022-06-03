package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.option.ItemOption;
import com.jay.shoppingmall.domain.item.option.ItemOptionRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.item.ItemDetailResponse;
import com.jay.shoppingmall.dto.response.ZzimResponse;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ImageRepository imageRepository;
    private final FileHandler fileHandler;
    private final ZzimRepository zzimRepository;
    private final ZzimService zzimService;
    private final ItemOptionRepository itemOptionRepository;

    public Slice<ItemResponse> itemAll(User user, Pageable pageable) {
//        List<ItemResponse> responses = new ArrayList<>();
        return itemRepository.findAll(pageable)
                .map(item -> ItemResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .price(item.getPrice())
//                          .salePrice(item.getSalePrice())
                        .zzim(item.getZzim())
                        .mainImage(fileHandler.getStringImage(imageRepository.findByItemIdAndIsMainImageTrue(item.getId())))
                        .isZzimed(user != null && zzimService.isZzimed(user.getId(), item.getId()))
                        .build());
    }


    public ItemDetailResponse itemDetail(User user, Long id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("해당 상품을 찾을 수 없습니다"));

        Map<String,List<String>> optionMap = new HashMap<>();
        final List<String> itemOptions = itemOptionRepository.findByItemId(item.getId()).stream().map(ItemOption::getOption1).distinct().collect(Collectors.toList());
//        final List<String> option1Distinct = itemOptions.stream().map(ItemOption::getOption1).distinct().collect(Collectors.toList());

        for (String option1 : itemOptions) {
            List<ItemOption> option2 = itemOptionRepository.findAllByOption1(option1);
            final List<String> option2list = option2.stream().map(ItemOption::getOption2).collect(Collectors.toList());
            optionMap.put(option1, option2list);
        }

        List<String> stringDescriptionImages = new ArrayList<>();
        List<Image> descriptionImages = imageRepository.findAllByItemId(item.getId());
        for (Image image : descriptionImages) {
            stringDescriptionImages.add(fileHandler.getStringImage(image));
        }

        item.viewCountUp();
        Image mainImage = imageRepository.findByItemIdAndIsMainImageTrue(item.getId());
        String stringMainImage = fileHandler.getStringImage(mainImage);

        return ItemDetailResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .optionMap(optionMap)
                .description(item.getDescription())
                .price(item.getPrice())
                .stock(item.getStock())
                .mainImage(stringMainImage)
                .descriptionImages(stringDescriptionImages)
                .zzim(item.getZzim())
                .isZzimed(user != null && zzimService.isZzimed(user.getId(), item.getId()))
                .build();
    }

    public Page<ItemResponse> searchItemsByKeyword(String keyword, Pageable pageable) {

        return itemRepository.findByNameContaining(keyword, pageable)
                .map(items -> items.map(item -> ItemResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .zzim(item.getZzim())
                        .mainImage(fileHandler.getStringImage(imageRepository.findByItemIdAndIsMainImageTrue(item.getId())))
                        .price(item.getPrice())
                        .salePrice(item.getSalePrice())
                        .build()))
                .orElseThrow(() -> new ItemNotFoundException("해당 키워드에 맞는 상품이 없습니다"));

    }

    public ZzimResponse itemZzim(final ItemZzimRequest request, final User user) {
        Item item = itemRepository.findById(request.getItemId()).orElseThrow(() -> new ItemNotFoundException("해당 상품을 찾을 수 없습니다"));
        Zzim zzim;
        if (zzimRepository.findByUserIdAndItemId(user.getId(), item.getId()) == null) {
            zzim = Zzim.builder()
                    .user(user)
                    .item(item)
                    .isZzimed(false)
                    .build();
            zzimRepository.saveAndFlush(zzim);
        } else {
            zzim = zzimRepository.findByUserIdAndItemId(user.getId(), item.getId());
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

        return ZzimResponse.builder()
                .zzimPerItem(item.getZzim())
                .isZzimed(zzim.getIsZzimed())
                .build();
    }
}