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
import com.jay.shoppingmall.domain.notification.NotificationRepository;
import com.jay.shoppingmall.domain.notification.me_notification.MeNotification;
import com.jay.shoppingmall.domain.notification.me_notification.MeNotificationRepository;
import com.jay.shoppingmall.domain.notification.model.NotificationType;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotification;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSellerRepository;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistory;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistoryRepository;
import com.jay.shoppingmall.domain.user.Role;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.dto.request.*;
import com.jay.shoppingmall.dto.request.qna.QnaAnswerRequest;
import com.jay.shoppingmall.dto.response.item.ItemSimpleResponse;
import com.jay.shoppingmall.dto.response.notification.NotificationResponse;
import com.jay.shoppingmall.dto.response.notification.QnaNotificationResponse;
import com.jay.shoppingmall.dto.response.order.OrderDetailResponse;
import com.jay.shoppingmall.dto.response.order.OrderItemResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentDetailResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentPerSellerResponse;
import com.jay.shoppingmall.dto.response.order.payment.RecentPaymentPerSellerResponse;
import com.jay.shoppingmall.dto.response.order.payment.RecentPaymentPerSellerSimpleResponse;
import com.jay.shoppingmall.dto.response.seller.SellerBankAccountHistoryResponse;
import com.jay.shoppingmall.dto.response.seller.SellerBankResponse;
import com.jay.shoppingmall.dto.response.seller.SellerDefaultSettingsResponse;
import com.jay.shoppingmall.dto.response.item.ItemTemporaryResponse;
import com.jay.shoppingmall.dto.response.seller.StatisticsResponse;
import com.jay.shoppingmall.exception.exceptions.*;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;
    private final QnaRepository qnaRepository;
    private final ItemTemporaryRepository itemTemporaryRepository;
    private final CartRepository cartRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final ItemPriceRepository itemPriceRepository;
    private final ItemStockRepository itemStockRepository;
    private final ItemStockHistoryRepository itemStockHistoryRepository;
    private final ItemPriceHistoryRepository itemPriceHistoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentPerSellerRepository paymentPerSellerRepository;
    private final OrderRepository orderRepository;
    private final SellerBankAccountHistoryRepository sellerBankAccountHistoryRepository;
    private final NotificationRepository<?> notificationRepository;
    private final MeNotificationRepository meNotificationRepository;


    private final FileHandler fileHandler;

    public Long writeItem(WriteItemRequest writeItemRequest, final MultipartFile file, final List<MultipartFile> files, final User user) {
        final Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        Item item = Item.builder()
                .name(writeItemRequest.getItemName())
                .description(writeItemRequest.getDescription())
                .brandName(writeItemRequest.getItemBrandName())
                .seller(seller)
                .build();
        final Long savedItem = itemRepository.save(item).getId();

        Image mainImage = fileHandler.parseFilesInfo(file, ImageRelation.ITEM_MAIN, item.getId());
        imageRepository.save(mainImage);

        //MultiPartFile은 input이 없을때 ''으로 들어오므로 아래와 같이 확인.
        if (files != null) {
            for (MultipartFile multipartFile : files) {
                imageRepository.save(fileHandler.parseFilesInfo(multipartFile, ImageRelation.ITEM_DESCRIPTION, item.getId()));
            }
        }

        ItemPrice itemPrice = getItemPrice(writeItemRequest.getSalePrice(), writeItemRequest.getOriginalPrice());

        ItemStock itemStock = getItemStock(writeItemRequest.getStock());

        ItemOption itemOption = ItemOption.builder()
                .option1("옵션없음")
                .option2("옵션없음")
                .isOptionMainItem(true)
                .itemStock(itemStock)
                .itemPrice(itemPrice)
                .item(item)
                .build();
        itemOptionRepository.save(itemOption);

        return savedItem;
    }

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
            final Long salePrice = optionValue.getOptionSalePrice();
            final Long optionOriginalPrice = optionValue.getOptionOriginalPrice();
            final Integer optionStock = optionValue.getOptionStock();

            ItemPrice itemPrice = getItemPrice(salePrice, optionOriginalPrice);

            ItemStock itemStock = getItemStock(optionStock);

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

    private ItemStock getItemStock(final Integer stock) {
        ItemStock itemStock = ItemStock.builder()
                .stock(stock)
                .build();
        itemStockRepository.save(itemStock);
        ItemStockHistory itemStockHistory = ItemStockHistory.builder()
                .itemStock(itemStock)
                .stock(itemStock.getStock())
                .stockChangedDate(LocalDateTime.now())
                .build();
        itemStockHistoryRepository.save(itemStockHistory);
        return itemStock;
    }

    private ItemPrice getItemPrice(final Long salePrice, final Long originalPrice) {
        ItemPrice itemPrice = ItemPrice.builder()
                .priceNow(salePrice)
                .originalPrice(originalPrice == null ? salePrice : originalPrice)
                .build();
        itemPriceRepository.save(itemPrice);
        ItemPriceHistory itemPriceHistory = ItemPriceHistory.builder()
                .itemPrice(itemPrice)
                .price(itemPrice.getPriceNow())
                .priceUpdateDate(LocalDateTime.now())
                .build();
        itemPriceHistoryRepository.save(itemPriceHistory);
        return itemPrice;
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
        Item item = qnaRepository.findById(qnaId)
                .map(Qna::getItem)
                .orElseThrow(() -> new ItemNotFoundException("해당 상품이 존재하지 않습니다"));

        if (this.sellerCheck(item.getId(), user)) {
            Qna qna = qnaRepository.findById(qnaId)
                    .orElseThrow(() -> new QnaException("QnA가 존재하지 않습니다"));
            qna.answerUpdate(qnaAnswerRequest.getAnswer());
        }
        //QnA 답변에 대한 notification to user
        final QnaNotification qnaNotification = notificationRepository.findByQnaId(qnaId);
        if (qnaNotification != null) {
            qnaNotification.answerUpdate(qnaAnswerRequest.getAnswer());

            MeNotification meNotification = MeNotification.builder()
                    .notificationType(NotificationType.QNA_ANSWER_TO_USER)
                    .originalMessage(qnaNotification.getMessage())
                    .sender(qnaNotification.getReceiver())
                    .receiver(qnaNotification.getSender())
                    .message(qnaAnswerRequest.getAnswer())
                    .item(item)
                    .build();

            meNotificationRepository.save(meNotification);
        }
    }

    public void temporarySave(final WriteItemRequest writeItemRequest, final User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        ItemTemporary itemTemporary = ItemTemporary.builder()
                .name(writeItemRequest.getItemName())
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
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        if (sellerRepository.existsByCompanyName(request.getCompanyName())) {
            if (!seller.getCompanyName().equals(request.getCompanyName()))
                throw new AlreadyExistsException("이미 사용 중인 회사명입니다");
        }

        final Address itemReleaseAddress = Address.builder()
                .address(request.getItemReleaseAddress())
                .detailAddress(request.getItemReleaseDetailAddress())
                .extraAddress(request.getItemReleaseExtraAddress())
                .zipcode(request.getItemReleaseZipcode())
                .build();

        if (Objects.equals(request.getItemReturnAddress(), "") || Objects.equals(request.getItemReturnDetailAddress(), "") || Objects.equals(request.getItemReturnZipcode(), "")) {
            seller.sellerDefaultUpdate(
                    request.getCompanyName(),
                    request.getContactNumber(),
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
                    request.getContactNumber(),
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
                .contactNumber(seller.getContactNumber())
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

    //전체 상품 가격, 상품 옵션과 개수, 개별 가격,남은 재고, 결제 정보, 받을 사람 주소 => 주문 상세 조회에서
    //세부적인 내용만 내려보내기
    public List<RecentPaymentPerSellerResponse> getSellerRecentOrders(final User user, Pageable pageable) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        final List<PaymentPerSeller> recentPayments = paymentPerSellerRepository.findBySellerIdOrderByCreatedDateDesc(seller.getId(), pageable);

        List<RecentPaymentPerSellerResponse> recentPaymentPerSellerResponses = new ArrayList<>();
        for (PaymentPerSeller paymentPerSeller : recentPayments) {
            final Integer itemTotalQuantityPerSeller = paymentPerSeller.getItemTotalQuantityPerSeller();

            final PaymentPerSellerResponse paymentPerSellerResponse = PaymentPerSellerResponse.builder()
                    .paymentPerSellerId(paymentPerSeller.getId())
                    .itemShippingFeePerSeller(paymentPerSeller.getItemShippingFeePerSeller())
                    .itemTotalQuantityPerSeller(itemTotalQuantityPerSeller)
                    .itemTotalPricePerSeller(paymentPerSeller.getItemTotalPricePerSeller())
                    .build();
            final Payment payment = paymentPerSeller.getPayment();

            final List<OrderItem> orderItems = orderItemRepository.findByOrderIdAndSellerId(paymentPerSeller.getOrder().getId(), seller.getId());
            final OrderItem mostExpensiveOneAtOrder = orderItems.stream().max(Comparator.comparingLong(OrderItem::getPriceAtPurchase))
                    .orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));
            String mainImage = fileHandler.getStringImage(imageRepository.findByImageRelationAndId(ImageRelation.ITEM_MAIN, mostExpensiveOneAtOrder.getMainImageId()));
            String mostExpensiveOne = mostExpensiveOneAtOrder.getItem().getName();
            String name = itemTotalQuantityPerSeller == 1 ? mostExpensiveOne : mostExpensiveOne + " 외 " + (itemTotalQuantityPerSeller - 1) + "건";

            RecentPaymentPerSellerSimpleResponse recentPaymentPerSellerSimpleResponse = RecentPaymentPerSellerSimpleResponse.builder()
                    .orderId(paymentPerSeller.getOrder().getId())
                    .orderDate(paymentPerSeller.getOrder().getCreatedDate())
                    .pg(payment.getPg().getName())
                    .payMethod(payment.getPayMethod().getName())
                    .merchantUid(payment.getMerchantUid())
                    .mainImage(mainImage)
                    .name(name)
                    .build();

            RecentPaymentPerSellerResponse recentPaymentPerSellerResponse = RecentPaymentPerSellerResponse.builder()
                    .recentPaymentPerSellerSimpleResponse(recentPaymentPerSellerSimpleResponse)
                    .paymentPerSellerResponse(paymentPerSellerResponse)
                    .build();
            recentPaymentPerSellerResponses.add(recentPaymentPerSellerResponse);
        }
        return recentPaymentPerSellerResponses;
    }

    public OrderDetailResponse treatOrders(final Long orderId, final User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("해당하는 주문이 없습니다"));
        Boolean isAllItemTrackingNumberIssued = true;

        LocalDateTime orderDate = order.getCreatedDate();
        //상품조회
        final List<OrderItem> orderItems = orderItemRepository.findByOrderIdAndSellerId(orderId, seller.getId());

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            boolean isTrackingStarted = false;

            final DeliveryStatus deliveryStatus = orderItem.getOrderDelivery().getDeliveryStatus();

            if (!deliveryStatus.equals(DeliveryStatus.PAYMENT_DONE)) {
                isTrackingStarted = true;
            } else {
                isAllItemTrackingNumberIssued = false;
            }
            final Image image = imageRepository.findByImageRelationAndId(ImageRelation.ITEM_MAIN, orderItem.getMainImageId());
            final String mainImage = fileHandler.getStringImage(image);

            OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                    .orderItemId(orderItem.getId())
                    .orderDate(orderDate)
                    .itemName(orderItem.getItem().getName())
                    .mainImage(mainImage)
                    .sellerCompanyName(orderItem.getSeller().getCompanyName())
                    .orderItemId(orderItem.getId())
                    .itemPrice(orderItem.getPriceAtPurchase())
                    .quantity(orderItem.getQuantity())
                    .deliveryStatus(deliveryStatus.getValue())
                    .isTrackingStarted(isTrackingStarted)
                    .build();

            orderItemResponses.add(orderItemResponse);
        }

        final PaymentPerSeller paymentPerSeller = paymentPerSellerRepository.findByOrderIdAndSellerId(orderId, seller.getId());

        final Payment payment = order.getPayment();

        PaymentDetailResponse paymentDetailResponse = PaymentDetailResponse.builder()
                .pg(payment.getPg().getName())
                .payMethod(payment.getPayMethod().getName())

                .paymentTotalPrice(paymentPerSeller.getItemTotalPricePerSeller())
                .paymentTotalShippingFee(paymentPerSeller.getItemShippingFeePerSeller())

                .buyerName(payment.getBuyerName())
                .buyerAddr("(" + payment.getBuyerPostcode() + ") " + payment.getBuyerAddr())
                .buyerEmail(payment.getBuyerEmail())
                .buyerTel(payment.getBuyerTel())

                .receiverName(payment.getReceiverInfo().getReceiverName())
                .receiverAddress("(" + payment.getReceiverInfo().getReceiverPostcode() + ") " + payment.getReceiverInfo().getReceiverAddress())
                .receiverEmail(payment.getReceiverInfo().getReceiverEmail())
                .receiverPhoneNumber(payment.getReceiverInfo().getReceiverPhoneNumber())
                .isAllItemTrackingNumberIssued(isAllItemTrackingNumberIssued)
                .build();

        return OrderDetailResponse.builder()
                .orderItemResponses(orderItemResponses)
                .paymentDetailResponse(paymentDetailResponse)
                .build();
    }

    public SellerBankResponse getSellerBalance(final User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        final List<SellerBankAccountHistory> transactionHistories = sellerBankAccountHistoryRepository.findTop20BySellerIdOrderByCreatedDateDesc(seller.getId());

        List<SellerBankAccountHistoryResponse> sellerBankAccountHistoryResponses = new ArrayList<>();
        //null 처리?
        for (SellerBankAccountHistory sellerBankAccountHistory : transactionHistories) {
            sellerBankAccountHistoryResponses.add(SellerBankAccountHistoryResponse.builder()
                    .transactionMoney(sellerBankAccountHistory.getTransactionMoney())
                    .build());
        }

        return SellerBankResponse.builder()
                .sellerId(seller.getId())
                .bankAccount(seller.getBankAccount())
                .sellerBankAccountHistoryResponses(sellerBankAccountHistoryResponses)
                .build();
    }

    public List<StatisticsResponse> getStatisticsByDay(final User user) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        List<StatisticsResponse> statisticsResponses = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.of(0,0,0));
            LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.of(23,59,59));

            final List<PaymentPerSeller> perDay = paymentPerSellerRepository.findBySellerIdAndCreatedDateBetween(seller.getId(), startDatetime, endDatetime);
            final long totalPricePerDay = perDay.stream().mapToLong(PaymentPerSeller::getItemTotalPricePerSeller).sum();
            final long totalQuantityPerDay = perDay.stream().mapToLong(PaymentPerSeller::getItemTotalQuantityPerSeller).sum();
            final long totalOrderPerDay = perDay.size();

            final StatisticsResponse statisticsResponse = StatisticsResponse.builder()
                    .date(startDatetime)
                    .totalPricePerDay(totalPricePerDay)
                    .totalQuantityPerDay(totalQuantityPerDay)
                    .totalOrderPerDay(totalOrderPerDay)
                    .build();
            statisticsResponses.add(statisticsResponse);
        }
        return statisticsResponses;
    }

    public void getItemRecentReviews(final User user, final Pageable pageable) {
        Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        //Seller를 기준으로 Review를 가져오려면 어떻게 해야할까??
        //Review나 QnA가 작성이 될때 메시지큐를 이용해서 셀러에게 알림을 준다. 이때 아이템 아이디와 유저명 등을 같이 보냄.
        //TODO RabbitMQ 공부 후 구현할 것.
    }

}
