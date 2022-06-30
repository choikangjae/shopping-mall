package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.browse_history.BrowseHistory;
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
import com.jay.shoppingmall.domain.review.Review;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.dto.request.ItemOptionRequest;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.ZzimResponse;
import com.jay.shoppingmall.dto.response.item.ItemDetailResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.review.ReviewStarCalculationResponse;
import com.jay.shoppingmall.exception.exceptions.StockInvalidException;
import com.jay.shoppingmall.service.handler.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Spy
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
    Seller seller;
    ItemOption itemOption;
    Item item;
    Item item2;
    ReviewStarCalculationResponse reviewStarCalculationResponse;
    List<ItemResponse> mockItemResponses;
    Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        seller = EntityBuilder.getSeller();
        itemOption = EntityBuilder.getItemOption();
        item = EntityBuilder.getItem();
        item2 = EntityBuilder.getItem2();

        reviewStarCalculationResponse = ReviewStarCalculationResponse.builder()
                .reviewAverageRating(3.5)
                .build();

        mockItemResponses = new ArrayList<>();
        ItemResponse itemResponse = mock(ItemResponse.class);
        mockItemResponses.add(itemResponse);
        mockItemResponses.add(itemResponse);
    }

    @Test
    void getAllItems() {
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

        when(itemRepository.findAll(Pageable.unpaged())).thenReturn(itemPage);
        doReturn(mockItemResponses).when(itemService).getItemResponses(any());

        final PageDto pageDto = itemService.getAllItems(user, Pageable.unpaged());

        assertThat(pageDto.getContent().size()).isEqualTo(2);
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
        final String KEYWORD = "키워드";

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);

        PageImpl<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(itemRepository.findByNameContaining(KEYWORD, Pageable.unpaged())).thenReturn(itemPage);
        doReturn(mockItemResponses).when(itemService).getItemResponses(any());

        final PageDto pageDto = itemService.searchItemsByKeyword(KEYWORD, user, Pageable.unpaged());

        assertThat(pageDto.getContent().size()).isEqualTo(2);
    }

    @Test
    void getItemResponses() {
        Image image1 = Image.builder()
                .imageRelation(ImageRelation.ITEM_MAIN)
                .foreignId(0L)
                .build();

        List<Item> items = new ArrayList<>();
        Item item = Item.builder()
                .id(0L)
                .build();
        Item item2 = Item.builder()
                .id(0L)
                .build();
        items.add(item);
        items.add(item2);

        when(itemOptionRepository.findByItemIdAndIsOptionMainItemTrue(any())).thenReturn(itemOption);
        when(imageRepository.findByImageRelationAndForeignId(any(), any())).thenReturn(image1);

        final List<ItemResponse> itemResponses = itemService.getItemResponses(items);

        assertThat(itemResponses.size()).isNotZero();
    }

    @Test
    void whenDeleteReturnsOne_ReturnsFalse_itemZzim() {
        Item item = Item.builder()
                .zzim(10)
                .build();

        ItemZzimRequest itemZzimRequest = ItemZzimRequest.builder()
                .itemId(0L)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(zzimRepository.deleteByUserIdAndItemId(any(), any())).thenReturn(1);

        final ZzimResponse zzimResponse = itemService.itemZzim(itemZzimRequest, user);

        assertThat(zzimResponse.getIsZzimed()).isFalse();
        assertThat(zzimResponse.getZzimPerItem()).isEqualTo(9);
    }

    @Test
    void whenDeleteReturnsZero_ReturnsTrue_itemZzim() {
        Item item = Item.builder()
                .zzim(10)
                .build();

        ItemZzimRequest itemZzimRequest = ItemZzimRequest.builder()
                .itemId(0L)
                .build();

        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(zzimRepository.deleteByUserIdAndItemId(any(), any())).thenReturn(0);

        final ZzimResponse zzimResponse = itemService.itemZzim(itemZzimRequest, user);

        assertThat(zzimResponse.getIsZzimed()).isTrue();
        assertThat(zzimResponse.getZzimPerItem()).isEqualTo(11);
    }

    @Test
    void itemOptionAddToList() {
        ItemOptionRequest itemOptionRequest = mock(ItemOptionRequest.class);

        when(itemOptionRepository.findByOption1AndOption2AndItemId(any(), any(), any())).thenReturn(Optional.ofNullable(itemOption));

        final ItemOptionResponse itemOptionResponse = itemService.itemOptionAddToList(itemOptionRequest);

        assertThat(itemOptionResponse.getOption1()).isEqualTo(itemOption.getOption1());
        assertThat(itemOptionResponse.getOption2()).isEqualTo(itemOption.getOption2());
        assertThat(itemOptionResponse.getItemQuantity()).isEqualTo(1);
    }

    @Test
    void whenRequestQuantityIsLessThanOrEqualToItemStock_ReturnResponse_itemOptionUpdate() {
        ItemOptionRequest itemOptionRequest = ItemOptionRequest.builder()
                .optionQuantity(5)
                .build();
        when(itemOptionRepository.findByOption1AndOption2AndItemId(any(), any(), any())).thenReturn(Optional.ofNullable(itemOption));

        final ItemOptionResponse itemOptionResponse = itemService.itemOptionUpdate(itemOptionRequest);

        assertThat(itemOptionResponse.getItemQuantity()).isEqualTo(5);
        assertThat(itemOptionResponse.getItemPrice()).isEqualTo(15000);
    }

    @Test
    void whenRequestQuantityIsGreaterThanItemStock_ThrowsStockInvalidException_itemOptionUpdate() {
        ItemOptionRequest itemOptionRequest = ItemOptionRequest.builder()
                .optionQuantity(51)
                .build();
        when(itemOptionRepository.findByOption1AndOption2AndItemId(any(), any(), any())).thenReturn(Optional.ofNullable(itemOption));

        final Exception exception = assertThrows(StockInvalidException.class, () -> itemService.itemOptionUpdate(itemOptionRequest));
        assertThat(exception.getMessage()).contains(itemOption.getItemStock().getStock().toString());
    }

    @Test
    void getAllMeZzim() {
        List<Zzim> zzims = new ArrayList<>();
        Zzim zzim = Zzim.builder()
                .item(item)
                .user(user)
                .isZzimed(false)
                .build();
        zzims.add(zzim);

        Page<Zzim> zzimPage = new PageImpl<>(zzims, pageable, zzims.size());

        ItemResponse itemResponse1 = ItemResponse.builder()
                .isZzimed(false)
                .build();
        ItemResponse itemResponse2 = ItemResponse.builder()
                .isZzimed(false)
                .build();
        List<ItemResponse> itemResponses = new ArrayList<>();
        itemResponses.add(itemResponse1);
        itemResponses.add(itemResponse2);

        when(zzimRepository.findByUser(any(), any())).thenReturn(zzimPage);
        doReturn(itemResponses).when(itemService).getItemResponses(any());

        final PageDto pageDto = itemService.getAllMeZzim(user, Pageable.unpaged());
        @SuppressWarnings("unchecked")
        List<ItemResponse> castedItemResponses = (List<ItemResponse>) pageDto.getContent();
        final List<ItemResponse> zzimedFalse = castedItemResponses.stream().filter(itemResponse -> !itemResponse.getIsZzimed()).collect(Collectors.toList());

        assertThat(pageDto.getContent().size()).isEqualTo(2);
        assertThat(zzimedFalse).isEmpty();
    }

    @Test
    void getMyBrowseHistories() {
        List<BrowseHistory> browseHistories = new ArrayList<>();
        List<ItemResponse> itemResponses = new ArrayList<>();

        for (long i = 0; i < 10; i++) {
            BrowseHistory browseHistory = BrowseHistory.builder()
                    .item(Item.builder().id(i).build())
                    .user(user)
                    .browseAt(LocalDateTime.now().minusMinutes(i))
                    .build();
            browseHistories.add(browseHistory);

            ItemResponse itemResponse = ItemResponse.builder()
                    .itemId(i)
                    .isZzimed(false)
                    .build();
            itemResponses.add(itemResponse);
        }

        Page<BrowseHistory> browseHistoryPage = new PageImpl<>(browseHistories, pageable, browseHistories.size());

        when(browseHistoryRepository.findAllByUserIdOrderByBrowseAtDesc(any(), any())).thenReturn(browseHistoryPage);
        doReturn(itemResponses).when(itemService).getItemResponses(any());

        final PageDto pageDto = itemService.getMyBrowseHistories(user, Pageable.unpaged());
        @SuppressWarnings("unchecked")
        List<ItemResponse> itemResponseList = (List<ItemResponse>) pageDto.getContent();

        for (int i = 0; i < itemResponseList.size(); i++) {
            assertThat(itemResponseList.get(0).getItemId()).isEqualTo(browseHistories.get(0).getItem().getId());
        }
        assertThat(itemResponseList.size()).isEqualTo(10);
    }

    @Test
    void showItemsBySeller() {
        List<ItemResponse> itemResponses = new ArrayList<>();
        ItemResponse itemResponse = mock(ItemResponse.class);
        itemResponses.add(itemResponse);
        itemResponses.add(itemResponse);

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);

        PageImpl<Item> itemPage = new PageImpl<>(items, pageable, items.size());

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(itemRepository.findBySellerId(any(), any())).thenReturn(itemPage);
        doReturn(itemResponses).when(itemService).getItemResponses(any());

        final PageDto pageDto = itemService.showItemsBySeller(user, Pageable.unpaged());

        assertThat(pageDto.getContent().size()).isEqualTo(2);
    }

    @Test
    void getSellerOtherItems() {
        List<ItemResponse> itemResponses = new ArrayList<>();
        ItemResponse itemResponse = mock(ItemResponse.class);
        itemResponses.add(itemResponse);
        itemResponses.add(itemResponse);
        itemResponses.add(itemResponse);

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(item2);

        PageImpl<Item> itemPage = new PageImpl<>(items, pageable, items.size());


        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        doReturn(itemResponses).when(itemService).getItemResponses(any());
        when(itemRepository.findBySellerId(any(), any())).thenReturn(itemPage);

        final PageDto sellerOtherItems = itemService.getSellerOtherItems(0L);

        assertThat(sellerOtherItems.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("zzim의 item id와 itemresponse의 item id가 같으면 zzim의 isZzimed를 itemResponse에 set한다")
    void whenZzimItemIdIsEqualToItemResponseItemId_SetIsZzimed_setIsZzimed() {
        List<ItemResponse> itemResponses = new ArrayList<>();
        List<Zzim> zzims = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            ItemResponse itemResponse = ItemResponse.builder()
                    .itemId(i)
                    .isZzimed(false)
                    .build();
            itemResponses.add(itemResponse);

            if (i % 2 == 0) {
                Item item = Item.builder()
                        .id(i)
                        .build();
                Zzim zzim = Zzim.builder()
                        .item(item)
                        .isZzimed(true)
                        .build();
                zzims.add(zzim);
            }
        }
        when(zzimRepository.findByItemIdIn(any())).thenReturn(zzims);

        itemService.setIsZzimed(user, itemResponses);

        final List<ItemResponse> itemResponseList = itemResponses.stream().filter(itemResponse -> !itemResponse.getIsZzimed()).collect(Collectors.toList());
        final List<ItemResponse> itemResponseListTrue = itemResponses.stream().filter(ItemResponse::getIsZzimed).collect(Collectors.toList());
        assertThat(itemResponseList.size()).isEqualTo(5);
        assertThat(itemResponseListTrue.size()).isEqualTo(5);
    }
}