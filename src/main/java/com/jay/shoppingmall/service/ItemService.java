package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.browse_history.BrowseHistory;
import com.jay.shoppingmall.domain.browse_history.BrowseHistoryRepository;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import com.jay.shoppingmall.domain.item.item_view_history.ItemViewHistoryRepository;
import com.jay.shoppingmall.domain.model.page.CustomPage;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.dto.request.ItemOptionRequest;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.StringImageResponse;
import com.jay.shoppingmall.dto.response.review.ReviewStarCalculationResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.item.ItemDetailResponse;
import com.jay.shoppingmall.dto.response.ZzimResponse;
import com.jay.shoppingmall.exception.exceptions.ImageNotFoundException;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.SellerNotFoundException;
import com.jay.shoppingmall.exception.exceptions.StockInvalidException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final SellerRepository sellerRepository;
    private final ImageRepository imageRepository;
    private final ZzimRepository zzimRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final BrowseHistoryRepository browseHistoryRepository;
    private final ItemViewHistoryRepository itemViewHistoryRepository;

    private final ReviewService reviewService;
    private final ZzimService zzimService;
    private final FileHandler fileHandler;
    private final SellerService sellerService;

    @Value("${host.url}")
    private String url;

    @Transactional(readOnly = true)
    public PageDto getAllItems(User user, Pageable pageable) {
        final Page<Item> itemPage = itemRepository.findAll(pageable);
        CustomPage customPage = new CustomPage(itemPage, url);

        List<Item> items = itemPage.getContent();
        List<ItemResponse> itemResponses = getItemResponses(items);

        setIsZzimed(user, itemResponses);

        return PageDto.builder()
                .customPage(customPage)
                .content(itemResponses)
                .build();
    }

    public void setIsZzimed(final User user, final List<ItemResponse> itemResponses) {
        if (user == null) return;

        final List<Long> itemIds = itemResponses.stream().map(ItemResponse::getItemId).collect(Collectors.toList());
        final List<Zzim> zzims = zzimRepository.findByItemIdIn(itemIds);

        for (ItemResponse itemResponse : itemResponses) {
            for (Zzim zzim : zzims) {
                if (itemResponse.getItemId().equals(zzim.getItem().getId())) {
                    itemResponse.setIsZzimed(zzim.getIsZzimed());
                    break;
                }
            }
        }
    }

    public List<ItemResponse> getItemResponses(final List<Item> items) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            final ReviewStarCalculationResponse reviewStarCalculationResponse = reviewService.reviewStarCalculation(item);

            ItemOption itemOption = itemOptionRepository.findByItemIdAndIsOptionMainItemTrue(item.getId());
            if (itemOption != null) {
                final ItemPrice itemPrice = itemOption.getItemPrice();
                final Long priceNow = itemPrice.getIsOnSale() ? itemPrice.getItemOnSale().getOnSalePrice() : itemPrice.getPriceNow();
                final Long originalPrice = itemPrice.getOriginalPrice();

                itemResponses.add(ItemResponse.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .priceNow(priceNow)
                        .originalPrice(originalPrice)
                        .zzim(item.getZzim())
                        .reviewStarCalculationResponse(reviewStarCalculationResponse)
                        .build());
            }
        }
        final List<Image> images = items.stream().map(item -> imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, item.getId())).collect(Collectors.toList());
        for (ItemResponse itemResponse : itemResponses) {
            final Image mainImage = images.stream().findFirst().filter(image -> image.getForeignId().equals(itemResponse.getItemId()))
                    .orElseThrow(() -> new ImageNotFoundException("사진이 존재하지 않습니다"));
            final String stringMainImage = fileHandler.getStringImage(mainImage);

            itemResponse.setMainImage(stringMainImage);
        }
        return itemResponses;
    }

    public ItemDetailResponse getItemDetail(User user, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("해당 상품을 찾을 수 없습니다"));
        final List<ItemOption> itemOptions = itemOptionRepository.findAllByItemId(item.getId());

        final ReviewStarCalculationResponse reviewStarCalculationResponse = reviewService.reviewStarCalculation(item);

        final Boolean isSellerItem = sellerService.sellerCheck(itemId, user);

        //TODO 성능 테스트로 밑의 코드와 현재 코드의 부하 테스트 진행해볼 것(100만, 1000만 기준)
        //리팩토링 이후
        final ItemOption mainItemOption = itemOptions.stream().filter(ItemOption::getIsOptionMainItem).findFirst()
                .orElseThrow(() -> new ItemNotFoundException("해당 옵션이 존재하지 않습니다"));
        Map<String, List<String>> optionMap = getOptionMap(itemOptions);

        final StringImageResponse stringImageResponse = getItemDetailStringImages(item);

        //상품 누적 조회수
        item.viewCountUp();

        //상품 조회 기록 저장/ 캐시 메모리로 전환 필요 ?
        setBrowseHistories(user, item);

        final boolean isZzimed = zzimService.isZzimed(user, item.getId());

        //리팩토링 전
//        final ItemOption mainOptionItem = itemOptionRepository.findByItemIdAndIsOptionMainItemTrue(itemId);
//        Map<String, List<String>> optionMap = new HashMap<>();
//        final List<String> itemOptions = itemOptionRepository.findByItemId(item.getId()).stream().map(ItemOption::getOption1).distinct().collect(Collectors.toList());
//
//        for (String option1 : itemOptions) {
//            List<ItemOption> option2 = itemOptionRepository.findAllByOption1AndItemId(option1, item.getId());
//            final List<String> option2list = option2.stream().map(ItemOption::getOption2).collect(Collectors.toList());
//            optionMap.put(option1, option2list);
//        }

//        final List<Image> images = imageRepository.findAllByForeignId(item.getId());
//        final Image mainImage = images.stream()
//                .filter(image -> image.getImageRelation().equals(ImageRelation.ITEM_MAIN))
//                .findFirst()
//                .orElseThrow(() -> new ImageNotFoundException("해당 상품의 사진이 존재하지 않습니다"));
//        final List<Image> descriptionImages = images.stream()
//                .filter(image -> image.getImageRelation().equals(ImageRelation.ITEM_DESCRIPTION))
//                .collect(Collectors.toList());
//
//        String stringMainImage = fileHandler.getStringImage(mainImage);
//        List<String> stringDescriptionImages = new ArrayList<>();
//        for (Image image : descriptionImages) {
//            stringDescriptionImages.add(fileHandler.getStringImage(image));
//        }
//        StringImageResponse stringImageResponse = StringImageResponse.builder()
//                .mainImage(stringMainImage)
//                .descriptionImageList(stringDescriptionImages)
//                .build();

//        List<Image> descriptionImages = imageRepository.findAllByImageRelationAndForeignId(ImageRelation.ITEM_DESCRIPTION, item.getId());
//        for (Image image : descriptionImages) {
//            stringDescriptionImages.add(fileHandler.getStringImage(image));
//        }
//        Image mainImage = imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, item.getId());
//        String stringMainImage = fileHandler.getStringImage(mainImage);


        //TODO 상품에 대한 조회수를 매일 그리고 시간별로 저장하는 최적화 방법 찾아보기.
        //상품 일자별 조회수
//        LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
//        LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
//        final ItemViewHistory itemView = itemViewHistoryRepository.findByItemIdAndViewDateBetween(item.getId(), startDatetime, endDatetime);
//        if (itemView != null) {
//            itemView.viewCountPerDayUp();
//        } else {
//            ItemViewHistory itemViewHistory = ItemViewHistory.builder()
//                    .viewCountPerDay(1)
//                    .viewDate(LocalDate.now())
//                    .viewTime(LocalTime.now())
//                    .item(item)
//                    .build();
//            itemViewHistoryRepository.save(itemViewHistory);
//        }


        return ItemDetailResponse.builder()
                .id(item.getId())
                .isSellerItem(isSellerItem)
                .name(item.getName())
                .brandName(item.getBrandName())
                .optionMap(optionMap)
                .description(item.getDescription())
                .reviewStarCalculationResponse(reviewStarCalculationResponse)
                .originalPrice(mainItemOption.getItemPrice().getOriginalPrice())
                .priceNow(mainItemOption.getItemPrice().getPriceNow())
                .stock(mainItemOption.getItemStock().getStock())
                .mainImage(stringImageResponse.getMainImage())
                .descriptionImages(stringImageResponse.getDescriptionImages())
                .zzim(item.getZzim())
                .isZzimed(isZzimed)
                .build();
    }

    private StringImageResponse getItemDetailStringImages(Item item) {
        final List<Image> images = imageRepository.findAllByForeignId(item.getId());
        final Image mainImage = images.stream()
                .filter(image -> image.getImageRelation().equals(ImageRelation.ITEM_MAIN))
                .findFirst()
                .orElseThrow(() -> new ImageNotFoundException("해당 상품의 사진이 존재하지 않습니다"));
        final List<Image> descriptionImages = images.stream()
                .filter(image -> image.getImageRelation().equals(ImageRelation.ITEM_DESCRIPTION))
                .collect(Collectors.toList());

        String stringMainImage = fileHandler.getStringImage(mainImage);
        List<String> stringDescriptionImages = new ArrayList<>();
        for (Image image : descriptionImages) {
            stringDescriptionImages.add(fileHandler.getStringImage(image));
        }
        return StringImageResponse.builder()
                .mainImage(stringMainImage)
                .descriptionImages(stringDescriptionImages)
                .build();
    }

    private void setBrowseHistories(final User user, final Item item) {
        if (user != null) {
            final List<BrowseHistory> browseHistories = browseHistoryRepository.findFirst20ByUserIdOrderByBrowseAtDesc(user.getId());

            for (BrowseHistory browseHistory : browseHistories) {
                if (Objects.equals(browseHistory.getItem().getId(), item.getId())) {
                    browseHistoryRepository.delete(browseHistory);
                }
            }
            if (browseHistories.size() >= 20) {
                final BrowseHistory history = browseHistories.get(browseHistories.size() - 1);
                browseHistoryRepository.delete(history);
            }
            BrowseHistory browseHistory = BrowseHistory.builder()
                    .item(item)
                    .user(user)
                    .browseAt(LocalDateTime.now())
                    .build();

            browseHistoryRepository.save(browseHistory);
        }
    }

    /**
     * getOptionMap returns a {@link Map} collection that contains {@link ItemOption#getOption1()} as
     * key and {@link List} of {@link ItemOption#getOption2()} as value.
     */
    private Map<String, List<String>> getOptionMap(final List<ItemOption> itemOptions) {
        final List<String> itemOption1List = itemOptions.stream().map(ItemOption::getOption1).distinct().collect(Collectors.toList());

        Map<String, List<String>> optionMap = new HashMap<>();
        for (String option1 : itemOption1List) {
            final List<String> itemOption2List = itemOptions.stream().filter(itemOption -> itemOption.getOption1().equals(option1)).map(ItemOption::getOption2).collect(Collectors.toList());
            optionMap.put(option1, itemOption2List);
        }
        return optionMap;
    }

    public PageDto searchItemsByKeyword(String keyword, final User user, Pageable pageable) {
        final Page<Item> itemPage = itemRepository.findByNameContaining(keyword, pageable);

        final List<ItemResponse> itemResponses = getItemResponses(itemPage.getContent());
        setIsZzimed(user, itemResponses);
        CustomPage customPage = new CustomPage(itemPage, "search");

        return PageDto.builder()
                .customPage(customPage)
                .content(itemResponses)
                .build();
    }

    public ZzimResponse itemZzim(final ItemZzimRequest request, final User user) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("해당 상품을 찾을 수 없습니다"));

        final int numberOfZzimDeleted = zzimRepository.deleteByUserIdAndItemId(user.getId(), item.getId());
        if (numberOfZzimDeleted == 1) {
            item.setZzim(item.getZzim() - 1);

            return ZzimResponse.builder()
                    .zzimPerItem(item.getZzim())
                    .isZzimed(false)
                    .build();
        }

        Zzim zzim = Zzim.builder()
                .user(user)
                .item(item)
                .isZzimed(true)
                .build();
        zzimRepository.save(zzim);

        item.setZzim(item.getZzim() + 1);

        return ZzimResponse.builder()
                .zzimPerItem(item.getZzim())
                .isZzimed(true)
                .build();
    }

    public ItemOptionResponse itemOptionAddToList(final ItemOptionRequest request) {
        final ItemOption itemOption = itemOptionRepository.findByOption1AndOption2AndItemId(request.getOption1(), request.getOption2(), request.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("잘못된 상품 접근입니다"));

        return ItemOptionResponse.builder()
                .itemId(itemOption.getItem().getId())
                .itemOptionId(itemOption.getId())
                .option1(itemOption.getOption1())
                .option2(itemOption.getOption2())
                .itemStock(itemOption.getItemStock().getStock())
                .itemQuantity(1)
                .itemPrice(itemOption.getItemPrice().getPriceNow())
                .build();
    }

    public ItemOptionResponse itemOptionUpdate(final ItemOptionRequest request) {
        final ItemOption itemOption = itemOptionRepository.findByOption1AndOption2AndItemId(request.getOption1(), request.getOption2(), request.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("잘못된 상품 접근입니다"));

        if (itemOption.getItemStock().getStock() < request.getOptionQuantity()) {
            throw new StockInvalidException("해당 상품의 재고는 " + itemOption.getItemStock().getStock() + " 개 입니다");
        }
        Long itemTotalPrice = request.getOptionQuantity() * itemOption.getItemPrice().getPriceNow();

        return ItemOptionResponse.builder()
                .option1(itemOption.getOption1())
                .option2(itemOption.getOption2())
                .itemQuantity(request.getOptionQuantity())
                .itemPrice(itemTotalPrice)
                .build();
    }

    public PageDto getAllMeZzim(User user, Pageable pageable) {
        Page<Zzim> zzims = zzimRepository.findByUser(user, pageable);

        final List<Item> items = zzims.stream().map(Zzim::getItem).collect(Collectors.toList());
        CustomPage customPage = new CustomPage(zzims, "");

        final List<ItemResponse> itemResponses = getItemResponses(items);
        for (ItemResponse itemResponse : itemResponses) {
            itemResponse.setIsZzimed(true);
        }

        return PageDto.builder()
                .content(itemResponses)
                .customPage(customPage)
                .build();
    }

    public PageDto getMyBrowseHistories(final User user, Pageable pageable) {
        final Page<BrowseHistory> browseHistoryPage = browseHistoryRepository.findAllByUserIdOrderByBrowseAtDesc(user.getId(), pageable);
        CustomPage customPage = new CustomPage(browseHistoryPage, "");
        final List<BrowseHistory> browseHistories = browseHistoryPage.getContent();

        final List<Item> items = browseHistories.stream().map(BrowseHistory::getItem).collect(Collectors.toList());
        final List<ItemResponse> itemResponses = getItemResponses(items);

        for (int i = 0; i < itemResponses.size(); i++) {
            itemResponses.get(i).setDateAt(browseHistories.get(i).getBrowseAt());
        }
        setIsZzimed(user, itemResponses);

        return PageDto.builder()
                .content(itemResponses)
                .customPage(customPage)
                .build();
    }

    public PageDto showItemsBySeller(final User user, final Pageable pageable) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자 권한이 없습니다"));

        final Page<Item> itemPageBySeller = itemRepository.findBySellerId(seller.getId(), pageable);
        CustomPage customPage = new CustomPage(itemPageBySeller, "");

        final List<ItemResponse> itemResponses = getItemResponses(itemPageBySeller.getContent());

        return PageDto.builder()
                .content(itemResponses)
                .customPage(customPage)
                .build();
    }

    public PageDto getSellerOtherItems(final Long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));
//        final List<Item> items = itemRepository.findFirst3BySellerId(item.getSellerId());
        final Page<Item> items = itemRepository.findBySellerId(item.getSellerId(), Pageable.unpaged());
        final List<Item> itemsContent = items.getContent();

        return PageDto.builder()
                .content(getItemResponses(itemsContent))
                .customPage(new CustomPage())
                .build();
    }
}