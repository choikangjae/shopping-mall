package com.jay.shoppingmall.service;

import com.jay.shoppingmall.common.model.OptionValue;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import com.jay.shoppingmall.domain.item.item_price.ItemPriceRepository;
import com.jay.shoppingmall.domain.item.item_price.price_history.ItemPriceHistory;
import com.jay.shoppingmall.domain.item.item_price.price_history.ItemPriceHistoryRepository;
import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import com.jay.shoppingmall.domain.item.item_stock.ItemStockRepository;
import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistory;
import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistoryRepository;
import com.jay.shoppingmall.domain.item.temporary.ItemTemporary;
import com.jay.shoppingmall.domain.item.temporary.ItemTemporaryRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.dto.request.*;
import com.jay.shoppingmall.dto.response.seller.RecentOrdersForSellerResponse;
import com.jay.shoppingmall.dto.response.seller.SellerDefaultSettingsResponse;
import com.jay.shoppingmall.dto.response.item.ItemResponse;
import com.jay.shoppingmall.dto.response.item.ItemTemporaryResponse;
import com.jay.shoppingmall.exception.exceptions.*;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final FileHandler fileHandler;
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final ZzimService zzimService;
    private final QnaRepository qnaRepository;
    private final ItemTemporaryRepository itemTemporaryRepository;
    private final CartRepository cartRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemStockRepository itemStockRepository;
    private final ItemStockHistoryRepository itemStockHistoryRepository;
    private final ItemPriceHistoryRepository itemPriceHistoryRepository;
    private final OrderItemRepository orderItemRepository;

    public Page<ItemResponse> showItemsBySeller(User user, Pageable pageable) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자 권한이 없습니다"));

        return itemRepository.findBySellerId(seller.getId(), pageable)
                .map(item -> ItemResponse.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .zzim(item.getZzim())
                        .mainImage(fileHandler.getStringImage(imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN,item.getId())))
                        .isZzimed(zzimService.isZzimed(user.getId(), item.getId()))
                        .build());

    }

    public Long writeItem(WriteItemRequest writeItemRequest, final MultipartFile file, final List<MultipartFile> files, final User user) {
        User userForId = userRepository.findById(user.getId())
                        .orElseThrow(() -> new UserNotFoundException("유저가 없습니다"));
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(userForId.getId())
                        .orElseThrow(() -> new SellerNotFoundException("판매자 자격이 없습니다"));

        Item item = Item.builder()
                .name(writeItemRequest.getName())
                .description(writeItemRequest.getDescription())
//                .price(writeItemRequest.getOriginalPrice())
//                .stock(writeItemRequest.getStock())
                .seller(seller)
                .build();

        Image mainImage = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, item.getId());
        imageRepository.save(mainImage);

        //MultiPartFile은 input이 없을때 ''으로 들어오므로 아래와 같이 확인.
        if (!files.get(0).isEmpty()) {
            for (MultipartFile multipartFile : files) {
                imageRepository.save(fileHandler.parseFilesInfo(multipartFile, ImageRelation.ITEM_DESCRIPTION, item.getId()));
            }
        }

        return itemRepository.save(item).getId();
    }

    //상품명, 설명 Item // 사진, 부가사진, Image// 여러개의 옵션들과 가격 재고 ItemOption이 가격과 재고를 들고있음. 가격변동까지.
    //상품이 삭제되더라도 Image의 썸네일은 남아있어 썸네일로 접근하면 됨?
    public Long writeOptionItem(final ApiWriteItemRequest apiWriteItemRequest, final List<OptionValue> optionValues, final MultipartFile file, final List<MultipartFile> files, final User user) {
        final Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));
        
        Item item = Item.builder()
                .description(apiWriteItemRequest.getDescription())
                .brandName(apiWriteItemRequest.getItemBrandName())
                .name(apiWriteItemRequest.getItemName())
                .seller(seller)
                .build();
        Item savedItem = itemRepository.save(item);

        //이미지의 아이디만 받아와서 아이디를 저장
        Image image = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, item.getId());
        imageRepository.save(image);

        //REST API로 올때는 ''이 아니라 null!
        if (files != null) {
            for (MultipartFile multipartFile : files) {
                imageRepository.save(fileHandler.parseFilesInfo(multipartFile, ImageRelation.ITEM_DESCRIPTION, item.getId()));
            }
        }

        for (OptionValue optionValue : optionValues) {
            ItemPrice itemPrice = ItemPrice.builder()
                    .priceNow(optionValue.getOptionSalePrice())
                    .originalPrice(optionValue.getOptionOriginalPrice() == null ? optionValue.getOptionSalePrice() : optionValue.getOptionOriginalPrice())
                    .build();
            itemPriceRepository.save(itemPrice);
            ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                    .itemPrice(itemPrice)
                    .price(itemPrice.getPriceNow())
                    .priceUpdateDate(LocalDateTime.now())
                    .build();
            itemPriceHistoryRepository.save(itemPriceHistory);

            ItemStock itemStock = ItemStock.builder()
                    .stock(optionValue.getOptionStock())
                    .build();
            itemStockRepository.save(itemStock);
            ItemStockHistory itemStockHistory = ItemStockHistory.builder()
                    .itemStock(itemStock)
                    .stock(itemStock.getStock())
                    .stockChangedDate(LocalDateTime.now())
                    .build();
            itemStockHistoryRepository.save(itemStockHistory);

            ItemOption itemOption = ItemOption.builder()
                    .option1(optionValue.getOption1())
                    .option2(optionValue.getOption2())
                    .isOptionMainItem(optionValue.getIsOptionMainItem())
                    .itemStock(itemStock)
                    .itemPrice(itemPrice)
                    .item(item)
                    .build();
            itemOptionRepository.save(itemOption);
        }
        return savedItem.getId();
    }


    public Boolean sellerAgreeCheck(final SellerAgreeRequest sellerAgreeRequest, final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("잘못된 요청입니다"));

        if (sellerRepository.findByUserIdAndIsActivatedTrue(id).isPresent()) {
            throw new AlreadyExistsException("이미 판매자로 가입이 되어있습니다");
        }

        user.updateUserRole(Role.ROLE_SELLER);

        Seller seller = Seller.builder()
                .userId(user.getId())
                .isLawAgree(sellerAgreeRequest.getIsLawAgree())
                .isSellerAgree(sellerAgreeRequest.getIsSellerAgree())
                .isActivated(true)
                .build();

        sellerRepository.save(seller);
        return true;
    }

    public void qnaAnswer(final QnaAnswerRequest qnaAnswerRequest, final User user) {
        Long qnaId = qnaAnswerRequest.getQnaId();
        Boolean isAnswered = qnaRepository.findById(qnaId)
                .map(Qna::getIsAnswered)
                .orElse(false);
        if (isAnswered) {
            throw new AlreadyExistsException("답변이 이미 존재합니다");
        }
        Long itemId = qnaRepository.findById(qnaId)
                .map(Qna::getItem)
                .map(Item::getId)
                .orElseThrow(() -> new ItemNotFoundException("해당 상품이 존재하지 않습니다"));

        if (this.sellerCheck(itemId, user)) {
            Qna qna = qnaRepository.findById(qnaId)
                    .orElseThrow(() -> new QnaException("QnA가 존재하지 않습니다"));
            qna.answerUpdate(qnaAnswerRequest.getAnswer());
        }

    }

    public void temporarySave(final WriteItemRequest writeItemRequest, final User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                        .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        ItemTemporary itemTemporary = ItemTemporary.builder()
                .name(writeItemRequest.getName())
                .description(writeItemRequest.getDescription())
                .price(writeItemRequest.getOriginalPrice())
                .salePrice(writeItemRequest.getSalePrice())
                .stock(writeItemRequest.getStock())
                .seller(seller)
                .build();

        itemTemporaryRepository.save(itemTemporary);
    }

    public List<ItemTemporaryResponse> retrieveItemTemporaries(final User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        return itemTemporaryRepository.findAllBySellerId(seller.getId()).stream()
                .map(itemTemporary -> ItemTemporaryResponse.builder()
                        .name(itemTemporary.getName())
                        .price(itemTemporary.getPrice())
                        .description(itemTemporary.getDescription())
                        .stock(itemTemporary.getStock())
                        .salePrice(itemTemporary.getSalePrice())
                        .build()).collect(Collectors.toList());
    }
    public Boolean sellerCheck(final Long itemId, final User user) {
        if (user == null) {
            return false;
        }
        Long sellerId = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .map(Seller::getId)
                .orElse(-1L);

        Long sellerId2 = itemRepository.findById(itemId)
                .map(Item::getSeller)
                .map(Seller::getId)
                .orElse(-2L);
        return Objects.equals(sellerId, sellerId2);
    }

    public void itemDelete(final User user, final Long itemId) {
        if (!this.sellerCheck(itemId, user)) {
            throw new SellerNotFoundException("판매자가 아닙니다");
        }
        itemRepository.deleteById(itemId);
        cartRepository.deleteByItemId(itemId);
    }

    public void sellerDefaultSettingSave(SellerDefaultSettingsRequest request, User user) {
        if (sellerRepository.existsByCompanyName(request.getCompanyName())) {
            throw new AlreadyExistsException("이미 사용 중인 회사명입니다");
        }
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        final Address itemReleaseAddress = Address.builder()
                .address(request.getItemReleaseAddress())
                .detailAddress(request.getItemReleaseDetailAddress())
                .extraAddress(request.getItemReleaseExtraAddress())
                .zipcode(request.getItemReleaseZipcode())
                .build();

        if (Objects.equals(request.getItemReturnAddress(), "") || Objects.equals(request.getItemReturnDetailAddress(), "") || Objects.equals(request.getItemReturnZipcode(), "") ) {
            seller.sellerDefaultUpdate(
                    request.getCompanyName(),
                    itemReleaseAddress,
                    itemReleaseAddress,
                    request.getShippingFeeDefault(),
                    request.getReturnShippingFeeDefault(),
                    request.getShippingFeeFreePolicy(),
                    request.getDefaultDeliveryCompany());
        } else {
            final Address itemReturnAddress = Address.builder()
                    .address(request.getItemReturnAddress())
                    .detailAddress(request.getItemReturnDetailAddress())
                    .extraAddress(request.getItemReturnExtraAddress())
                    .zipcode(request.getItemReturnZipcode())
                    .build();

            seller.sellerDefaultUpdate(
                    request.getCompanyName(),
                    itemReleaseAddress,
                    itemReturnAddress,
                    request.getShippingFeeDefault(),
                    request.getReturnShippingFeeDefault(),
                    request.getShippingFeeFreePolicy(),
                    request.getDefaultDeliveryCompany());
        }
    }

    public SellerDefaultSettingsResponse sellerDefaultSettings(User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        return SellerDefaultSettingsResponse.builder()
                .companyName(seller.getCompanyName())
                .shippingFeeDefault(seller.getShippingFeeDefault())
                .shippingFeeFreePolicy(seller.getShippingFeeFreePolicy())

                .itemReleaseZipcode(seller.getItemReleaseAddress().getZipcode())
                .itemReleaseAddress(seller.getItemReleaseAddress().getAddress())
                .itemReleaseDetailAddress(seller.getItemReleaseAddress().getDetailAddress())
                .itemReleaseExtraAddress(seller.getItemReleaseAddress().getExtraAddress())

                .itemReturnZipcode(seller.getItemReturnAddress().getZipcode())
                .itemReturnAddress(seller.getItemReturnAddress().getAddress())
                .itemReturnDetailAddress(seller.getItemReturnAddress().getDetailAddress())
                .itemReturnExtraAddress(seller.getItemReturnAddress().getExtraAddress())

                .defaultDeliveryCompany(seller.getDefaultDeliveryCompany())
                .returnShippingFeeDefault(seller.getReturnShippingFeeDefault())
                .build();
    }

    public boolean sellerDefaultSettingCheck(final User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));
        return seller.getCompanyName() != null;
    }

    public void getSellerRecentOrders(final User user, Pageable pageable) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));
        Long itemOrderPriceAtPurchaseTotal = 0L;
        final List<OrderItem> orderItems = orderItemRepository.findBySellerId(seller.getId(), pageable);
        //전체 상품 가격, 상품 옵션과 개수, 개별 가격,남은 재고, 결제 정보, 받을 사람 주소 => 주문 상세 조회에서
        //세부적인 내용만 내려보내기
        for (OrderItem orderItem : orderItems) {
            RecentOrdersForSellerResponse.builder().build();
            orderItem.getQuantity();

            itemOrderPriceAtPurchaseTotal += orderItem.getPriceAtPurchase();

        }
    }
}
