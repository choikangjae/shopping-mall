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
import com.jay.shoppingmall.domain.item.item_price.ItemPriceRepository;
import com.jay.shoppingmall.domain.item.item_stock.ItemStockRepository;
import com.jay.shoppingmall.domain.model.page.CustomPage;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.review.ReviewRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.zzim.Zzim;
import com.jay.shoppingmall.domain.zzim.ZzimRepository;
import com.jay.shoppingmall.dto.request.ItemOptionRequest;
import com.jay.shoppingmall.dto.request.ItemZzimRequest;
import com.jay.shoppingmall.dto.response.ReviewStarCalculationResponse;
import com.jay.shoppingmall.dto.response.item.ItemOptionResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.item.ItemDetailResponse;
import com.jay.shoppingmall.dto.response.ZzimResponse;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.SellerNotFoundException;
import com.jay.shoppingmall.exception.exceptions.StockInvalidException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;

import lombok.ToString;
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
@ToString
public class ItemService {

    private final ItemRepository itemRepository;
    private final SellerRepository sellerRepository;
    private final ImageRepository imageRepository;
    private final FileHandler fileHandler;
    private final ZzimRepository zzimRepository;
    private final ZzimService zzimService;
    private final ItemOptionRepository itemOptionRepository;
    private final ItemStockRepository itemStockRepository;
    private final ItemPriceRepository itemPriceRepository;
    private final ReviewRepository reviewRepository;
    private final BrowseHistoryRepository browseHistoryRepository;

    public ReviewStarCalculationResponse reviewStarCalculation(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("해당 상품을 찾을 수 없습니다"));

        double reviewAverageRating = item.getReviewAverageRating() == null ? 0.0 : item.getReviewAverageRating();
        double fullStar = Math.floor(reviewAverageRating);
        double halfStar = Math.round((reviewAverageRating - fullStar) * 100) / 100.0;
        double emptyStar = 5 - Math.ceil(reviewAverageRating);

        if (halfStar < 0.4 && halfStar > 0.0) {
            halfStar = 0.0;
            emptyStar++;
        } else if (halfStar >= 0.8) {
            halfStar = 0.0;
            fullStar++;
        } else if (halfStar >= 0.4) {
            halfStar = 1.0;
        }
        return ReviewStarCalculationResponse.builder()
                .reviewAverageRating(reviewAverageRating)
                .fullStar(fullStar)
                .halfStar(halfStar)
                .emptyStar(emptyStar)
                .build();
    }

    public PageDto itemAll(User user, Pageable pageable) {
        final Page<Item> itemPage = itemRepository.findAll(pageable);
        CustomPage customPage = new CustomPage(itemPage);

        List<Item> items = itemPage.getContent();
        List<ItemResponse> itemResponses = getItemResponses(items);

        setItemsZzimBoolean(user, itemResponses);

        return PageDto.builder()
                .customPage(customPage)
                .content(itemResponses)
                .build();
//        return itemRepository.findAll(pageable)
//                .map(item -> ItemResponse.builder()
//                        .id(item.getId())
//                        .name(item.getName())
//                        .priceNow(item.getPrice())
//                        .originalPrice()
//                        .zzim(item.getZzim())
//                        .mainImage(fileHandler.getStringImage(imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN,item.getId())))
//                        .isZzimed(user != null && zzimService.isZzimed(user.getId(), item.getId()))
//                        .build());
    }

    private void setItemsZzimBoolean(final User user, final List<ItemResponse> itemResponses) {
        if (user != null)
            for (ItemResponse itemResponse : itemResponses) {
                itemResponse.setIsZzimed(zzimService.isZzimed(user.getId(), itemResponse.getItemId()));
            }
    }

    private List<ItemResponse> getItemResponses(final List<Item> items) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        for (Item item : items) {
            final ReviewStarCalculationResponse reviewStarCalculationResponse = this.reviewStarCalculation(item.getId());

            ItemOption itemOption = itemOptionRepository.findByItemIdAndIsOptionMainItemTrue(item.getId());
            if (itemOption != null) {
                final ItemPrice itemPrice = itemOption.getItemPrice();
                final Long priceNow = itemPrice.getIsOnSale() != null ? itemPrice.getItemOnSale().getOnSalePrice() : itemPrice.getPriceNow();
                final Long originalPrice = itemPrice.getOriginalPrice();

                itemResponses.add(ItemResponse.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .priceNow(priceNow)
                        .originalPrice(originalPrice)
                        .zzim(item.getZzim())
                        .reviewStarCalculationResponse(reviewStarCalculationResponse)
                        .mainImage(fileHandler.getStringImage(imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, item.getId())))
                        .build());
            }
        }
        return itemResponses;
    }


    public ItemDetailResponse itemDetail(User user, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("해당 상품을 찾을 수 없습니다"));
        final ReviewStarCalculationResponse reviewStarCalculationResponse = reviewStarCalculation(item.getId());

        //대표 상품
        final ItemOption mainOptionItem = itemOptionRepository.findByItemIdAndIsOptionMainItemTrue(itemId);
//        List<ItemOption> optionItemAll = itemOptionRepository.findByItemId(itemId);

        Map<String, List<String>> optionMap = new HashMap<>();
        final List<String> itemOptions = itemOptionRepository.findByItemId(item.getId()).stream().map(ItemOption::getOption1).distinct().collect(Collectors.toList());

        for (String option1 : itemOptions) {
            List<ItemOption> option2 = itemOptionRepository.findAllByOption1AndItemId(option1, item.getId());
            final List<String> option2list = option2.stream().map(ItemOption::getOption2).collect(Collectors.toList());
            optionMap.put(option1, option2list);
        }

        //이미지
        List<String> stringDescriptionImages = new ArrayList<>();
        List<Image> descriptionImages = imageRepository.findAllByImageRelationAndForeignId(ImageRelation.ITEM_DESCRIPTION, item.getId());
        for (Image image : descriptionImages) {
            stringDescriptionImages.add(fileHandler.getStringImage(image));
        }
        Image mainImage = imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, item.getId());
        String stringMainImage = fileHandler.getStringImage(mainImage);

        //조회수
        item.viewCountUp();

        //상품 조회 기록 저장/ 캐시 메모리로 전환 필요 ?
        if (user != null) {
            final List<BrowseHistory> browseHistories = browseHistoryRepository.findAllByUserIdOrderByBrowseAtDesc(user.getId());
            final List<Long> itemIds = browseHistories.stream().map(BrowseHistory::getItem).map(Item::getId).collect(Collectors.toList());

            if (browseHistories.size() >= 20) {
                final BrowseHistory history = browseHistories.get(browseHistories.size() - 1);
                browseHistoryRepository.delete(history);
            }
            if (itemIds.contains(itemId)) {
                BrowseHistory history = browseHistoryRepository.findByItemId(itemId);
                browseHistoryRepository.delete(history);
            }

            BrowseHistory browseHistory = BrowseHistory.builder()
                    .item(item)
                    .user(user)
                    .browseAt(LocalDateTime.now())
                    .build();

            //기록 최대 20개
            browseHistoryRepository.save(browseHistory);
            browseHistoryRepository.save(browseHistory);
        }

        return ItemDetailResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .brandName(item.getBrandName())
                .optionMap(optionMap)
                .description(item.getDescription())
                .reviewStarCalculationResponse(reviewStarCalculationResponse)
                .originalPrice(mainOptionItem.getItemPrice().getOriginalPrice())
                .priceNow(mainOptionItem.getItemPrice().getPriceNow())
                .stock(mainOptionItem.getItemStock().getStock())

                .mainImage(stringMainImage)
                .descriptionImages(stringDescriptionImages)
                .zzim(item.getZzim())
                .isZzimed(user != null && zzimService.isZzimed(user.getId(), item.getId()))
                .build();
    }

    public PageDto searchItemsByKeyword(String keyword, final User user, Pageable pageable) {
        final Page<Item> itemPage = itemRepository.findByNameContaining(keyword, pageable)
                .orElseThrow(() -> new ItemNotFoundException("키워드에 해당하는 상품이 없습니다"));
        final List<ItemResponse> itemResponses = getItemResponses(itemPage.getContent());
        setItemsZzimBoolean(user, itemResponses);
        CustomPage customPage = new CustomPage(itemPage);

        return PageDto.builder()
                .customPage(customPage)
                .content(itemResponses)
                .build();
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

    public ItemOptionResponse itemOptionAddToList(final ItemOptionRequest request, final User user) {
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
        Page<Zzim> zzims = zzimRepository.findByUser(user, pageable)
                .orElseThrow(() -> new ItemNotFoundException("아이디에 상품이 없습니다"));
        final List<Item> items = zzims.stream().map(Zzim::getItem).collect(Collectors.toList());
        CustomPage customPage = new CustomPage(zzims);

        final List<ItemResponse> itemResponses = getItemResponses(items);
        setItemsZzimBoolean(user, itemResponses);
        final List<ItemResponse> responses = itemResponses.stream().filter(ItemResponse::getIsZzimed).collect(Collectors.toList());

//        for (Zzim zzim : zzims) {
//            if (!zzim.getIsZzimed()) {
//                continue;
//            }
//            Item item = itemRepository.findById(zzim.getItem().getId())
//                    .orElseThrow(() -> new ItemNotFoundException("상품이 없습니다"));
//
//            itemResponses.add(ItemResponse.builder()
//                    .itemId(item.getId())
//                    .name(item.getName())
//                    .zzim(item.getZzim())
//                    .mainImage(fileHandler.getStringImage(imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN,item.getId())))
//                    .priceNow(item.getPrice())
//                    .originalPrice(item.getSalePrice())
//                    .isZzimed(zzim.getIsZzimed())
//                    .build());
//        }


        return PageDto.builder()
                .content(responses)
                .customPage(customPage)
                .build();
    }


    public PageDto getMyBrowseHistories(final User user, Pageable pageable) {
        final Page<BrowseHistory> browseHistoryPage = browseHistoryRepository.findAllByUserIdOrderByBrowseAtDesc(user.getId(), pageable);
        CustomPage customPage = new CustomPage(browseHistoryPage);
        final List<BrowseHistory> browseHistories = browseHistoryPage.getContent();

        final List<Item> items = browseHistories.stream().map(BrowseHistory::getItem).collect(Collectors.toList());
        final List<ItemResponse> itemResponses = getItemResponses(items);

        //리팩토링하기.
        for (ItemResponse itemResponse : itemResponses) {
            for (BrowseHistory browseHistory : browseHistories) {
                if (Objects.equals(itemResponse.getItemId(), browseHistory.getItem().getId())) {
                    itemResponse.setDateAt(browseHistory.getBrowseAt());
                }
            }
        }
        setItemsZzimBoolean(user, itemResponses);

        return PageDto.builder()
                .content(itemResponses)
                .customPage(customPage)
                .build()
                ;
    }

    public PageDto showItemsBySeller(final User user, final Pageable pageable) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자 권한이 없습니다"));

        final Page<Item> itemPageBySeller = itemRepository.findBySellerId(seller.getId(), pageable);
        CustomPage customPage = new CustomPage(itemPageBySeller);

        final List<ItemResponse> itemResponses = getItemResponses(itemPageBySeller.getContent());

        return PageDto.builder()
                .content(itemResponses)
                .customPage(customPage)
                .build()
                ;
    }
}