package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.browse_history.BrowseHistoryRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.dto.response.item.ItemDetailResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.review.ReviewStarCalculationResponse;
import com.jay.shoppingmall.service.handler.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    ItemService itemService;

    @Mock
    ItemRepository itemRepository;
    @Mock
    SellerRepository sellerRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    ZzimRepository zzimRepository;
    @Mock
    ItemOptionRepository itemOptionRepository;
    @Mock
    BrowseHistoryRepository browseHistoryRepository;

    @Mock
    ReviewService reviewService;
    @Mock
    ZzimService zzimService;
    @Mock
    FileHandler fileHandler;
    @Mock
    SellerService sellerService;

    User user;
    ItemOption itemOption;
    Item item;
    Item item2;
    ReviewStarCalculationResponse reviewStarCalculationResponse;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        itemOption = EntityBuilder.getItemOption();
        item = EntityBuilder.getItem();
        item2 = EntityBuilder.getItem2();

        reviewStarCalculationResponse = ReviewStarCalculationResponse.builder()
                .reviewAverageRating(3.5)
                .build();
    }

    @Test
    void getAllItems() {
        Image image = Image.builder()
                .imageRelation(ImageRelation.ITEM_MAIN)
                .foreignId(0L)
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        List<Item> items = new ArrayList<>();
        Item item = Item.builder()
                .id(0L)
                .build();
        Item item2 = Item.builder()
                .id(0L)
                .build();

        items.add(item);
        items.add(item2);
        PageImpl<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(imageRepository.findByImageRelationAndForeignId(any(), any())).thenReturn(image);
        when(itemRepository.findAll(Pageable.unpaged())).thenReturn(itemPage);
        when(itemOptionRepository.findByItemIdAndIsOptionMainItemTrue(any())).thenReturn(itemOption);
        when(zzimService.isZzimed(any(), any())).thenReturn(true);
        when(reviewService.reviewStarCalculation(any())).thenReturn(reviewStarCalculationResponse);

        final PageDto pageDto = itemService.getAllItems(user, Pageable.unpaged());

        @SuppressWarnings("unchecked")
        List<ItemResponse> itemResponses = (List<ItemResponse>) pageDto.getContent();

        assertThat(itemResponses.size()).isEqualTo(2);
        assertThat(itemResponses.get(0).getIsZzimed()).isTrue();
        System.out.println(itemResponses.get(0).getMainImage());
    }

    @Test
    void getItemDetail() {
        Image image1 = Image.builder()
                .imageRelation(ImageRelation.ITEM_MAIN)
                .foreignId(0L)
                .build();
        Image image2 = Image.builder()
                .imageRelation(ImageRelation.ITEM_DESCRIPTION)
                .foreignId(0L)
                .build();
        Image image3 = Image.builder()
                .imageRelation(ImageRelation.ITEM_DESCRIPTION)
                .foreignId(0L)
                .build();

        List<Image> images = new ArrayList<>();
        images.add(image1);
        images.add(image2);
        images.add(image3);

        List<ItemOption> itemOptions = new ArrayList<>();
        itemOptions.add(itemOption);
        itemOptions.add(itemOption);
        itemOptions.add(itemOption);

        when(reviewService.reviewStarCalculation(any())).thenReturn(reviewStarCalculationResponse);
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(imageRepository.findAllByForeignId(any())).thenReturn(images);
        when(itemOptionRepository.findAllByItemId(any())).thenReturn(itemOptions);

        final ItemDetailResponse itemDetailResponse = itemService.getItemDetail(user, 0L);

        assertThat(itemDetailResponse).isNotNull();
    }

    @Test
    void searchItemsByKeyword() {


    }

    @Test
    void itemZzim() {
    }

    @Test
    void itemOptionAddToList() {
    }

    @Test
    void itemOptionUpdate() {
    }

    @Test
    void getAllMeZzim() {
    }

    @Test
    void getMyBrowseHistories() {
    }

    @Test
    void showItemsBySeller() {
    }

    @Test
    void getSellerOtherItems() {
    }
}