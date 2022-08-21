package com.jay.shoppingmall.service;

import com.jay.shoppingmall.common.model.OptionValue;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.item.item_price.ItemPriceRepository;
import com.jay.shoppingmall.domain.item.item_price.price_history.ItemPriceHistoryRepository;
import com.jay.shoppingmall.domain.item.item_stock.ItemStockRepository;
import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistoryRepository;
import com.jay.shoppingmall.domain.item.temporary.ItemTemporary;
import com.jay.shoppingmall.domain.item.temporary.ItemTemporaryRepository;
import com.jay.shoppingmall.domain.notification.NotificationRepository;
import com.jay.shoppingmall.domain.notification.me_notification.MeNotificationRepository;
import com.jay.shoppingmall.domain.notification.model.NotificationType;
import com.jay.shoppingmall.domain.notification.qna_notification.QnaNotification;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.model.PayMethod;
import com.jay.shoppingmall.domain.payment.model.Pg;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSellerRepository;
import com.jay.shoppingmall.domain.qna.Qna;
import com.jay.shoppingmall.domain.qna.QnaCategory;
import com.jay.shoppingmall.domain.qna.QnaRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistory;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistoryRepository;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.TransactionType;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.user.UserRepository;
import com.jay.shoppingmall.domain.user.model.Address;
import com.jay.shoppingmall.dto.request.ApiWriteItemRequest;
import com.jay.shoppingmall.dto.request.SellerAgreeRequest;
import com.jay.shoppingmall.dto.request.SellerDefaultSettingsRequest;
import com.jay.shoppingmall.dto.request.WriteItemRequest;
import com.jay.shoppingmall.dto.request.qna.QnaAnswerRequest;
import com.jay.shoppingmall.dto.response.item.ItemTemporaryResponse;
import com.jay.shoppingmall.dto.response.order.OrderDetailResponse;
import com.jay.shoppingmall.dto.response.order.payment.RecentPaymentPerSellerResponse;
import com.jay.shoppingmall.dto.response.seller.SellerBankResponse;
import com.jay.shoppingmall.dto.response.seller.SellerDefaultSettingsResponse;
import com.jay.shoppingmall.dto.response.seller.StatisticsResponse;
import com.jay.shoppingmall.exception.exceptions.AlreadyExistsException;
import com.jay.shoppingmall.exception.exceptions.NotValidException;
import com.jay.shoppingmall.service.handler.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Spy
    @InjectMocks
    SellerService sellerService;

    @Mock
    SellerRepository sellerRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    QnaRepository qnaRepository;
    @Mock
    ItemTemporaryRepository itemTemporaryRepository;
    @Mock
    CartRepository cartRepository;
    @Mock
    ItemOptionRepository itemOptionRepository;
    @Mock
    ItemPriceRepository itemPriceRepository;
    @Mock
    ItemStockRepository itemStockRepository;
    @Mock
    ItemStockHistoryRepository itemStockHistoryRepository;
    @Mock
    ItemPriceHistoryRepository itemPriceHistoryRepository;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    PaymentPerSellerRepository paymentPerSellerRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    SellerBankAccountHistoryRepository sellerBankAccountHistoryRepository;
    @Mock
    NotificationRepository<?> notificationRepository;
    @Mock
    MeNotificationRepository meNotificationRepository;

    @Mock
    FileHandler fileHandler;

    WriteItemRequest writeItemRequest;
    ApiWriteItemRequest apiWriteItemRequest;
    User user;
    Seller seller;
    Item item;

    @Mock
    MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        seller = EntityBuilder.getSeller();

        apiWriteItemRequest = ApiWriteItemRequest.builder()
                .itemBrandName("나이키")
                .itemName("운동화")
                .description("설명")
                .build();

        writeItemRequest = WriteItemRequest.builder()
                .itemBrandName("나이키")
                .itemName("운동화")
                .description("설명")
                .originalPrice(5000L)
                .salePrice(3000L)
                .build();

        item = Item.builder()
                .id(0L)
                .name(writeItemRequest.getItemName())
                .description(writeItemRequest.getDescription())
                .brandName(writeItemRequest.getItemBrandName())
                .seller(seller)
                .build();
    }

    @Test
    void whenDescriptionImagesAreNotNull_AllImagesWillBeSaved_writeItem() {
        Image image = Image.builder().build();
        List<MultipartFile> multipartFileList = new ArrayList<>();
        multipartFileList.add(multipartFile);
        multipartFileList.add(multipartFile);

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(itemRepository.save(any())).thenReturn(item);
        when(fileHandler.parseFilesInfo(any(), any(), any())).thenReturn(image);

        final Long itemId = sellerService.writeItem(writeItemRequest, multipartFile, multipartFileList, user);

        verify(imageRepository, times(3)).save(any());
        verify(itemOptionRepository).save(any());
        assertThat(itemId).isEqualTo(0);
    }

    @Test
    void whenDescriptionImagesAreNull_MainImageWillBeSaved_writeItem() {
        Image image = Image.builder().build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(itemRepository.save(any())).thenReturn(item);
        when(fileHandler.parseFilesInfo(any(), any(), any())).thenReturn(image);

        final Long itemId = sellerService.writeItem(writeItemRequest, multipartFile, null, user);

        verify(imageRepository).save(any());
        verify(itemOptionRepository).save(any());
        assertThat(itemId).isEqualTo(0);
    }

    @Test
    void whenOptionItemsWrite_WillBeSaved_OptionItem() {
        OptionValue optionValue = OptionValue.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(true)
                .optionOriginalPrice(5000L)
                .optionSalePrice(3000L)
                .optionStock(5)
                .build();
        OptionValue optionValue2 = OptionValue.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(false)
                .optionOriginalPrice(5000L)
                .optionSalePrice(3000L)
                .optionStock(5)
                .build();
        List<OptionValue> optionValues = new ArrayList<>();
        optionValues.add(optionValue);
        optionValues.add(optionValue2);

        List<MultipartFile> multipartFileList = new ArrayList<>();
        multipartFileList.add(multipartFile);
        multipartFileList.add(multipartFile);

        Image image = Image.builder().build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(itemRepository.save(any())).thenReturn(item);
        when(fileHandler.parseFilesInfo(any(), any(), any())).thenReturn(image);

        final Long itemId = sellerService.writeOptionItem(apiWriteItemRequest, optionValues, multipartFile, multipartFileList, user);

        verify(imageRepository, times(3)).save(any());
        verify(itemOptionRepository, times(2)).save(any());
        assertThat(itemId).isEqualTo(0L);
    }

    @Test
    void whenMainItemTrueIsNotOne_ThrowNotValidException_OptionItem() {
        OptionValue optionValue = OptionValue.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(true)
                .optionOriginalPrice(5000L)
                .optionSalePrice(3000L)
                .optionStock(5)
                .build();
        OptionValue optionValue2 = OptionValue.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(true)
                .optionOriginalPrice(5000L)
                .optionSalePrice(3000L)
                .optionStock(5)
                .build();
        List<OptionValue> optionValues = new ArrayList<>();
        optionValues.add(optionValue);
        optionValues.add(optionValue2);

        List<MultipartFile> multipartFileList = new ArrayList<>();
        multipartFileList.add(multipartFile);
        multipartFileList.add(multipartFile);

        Image image = Image.builder().build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));

        assertThrows(NotValidException.class, () -> sellerService.writeOptionItem(apiWriteItemRequest, optionValues, multipartFile, multipartFileList, user));
    }

    @Test
    void whenUserIsNull_ReturnFalse_sellerCheck() {
        final Boolean isSeller = sellerService.sellerCheck(item.getId(), null);

        assertThat(isSeller).isFalse();
    }

    @Test
    void whenUserSellerIdIsEqualToItemSellerId_ReturnTrue_sellerCheck() {
        seller = Seller.builder()
                .id(3L)
                .build();
        item = Item.builder()
                .id(0L)
                .name(writeItemRequest.getItemName())
                .description(writeItemRequest.getDescription())
                .brandName(writeItemRequest.getItemBrandName())
                .seller(seller)
                .build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        final Boolean isSeller = sellerService.sellerCheck(item.getId(), user);

        assertThat(isSeller).isTrue();
    }
    @Test
    void whenUserSellerIdIsNotEqualToItemSellerId_ReturnFalse_sellerCheck() {
        seller = Seller.builder()
                .id(3L)
                .build();
        Seller seller2 = Seller.builder()
                .id(4L)
                .build();

        item = Item.builder()
                .id(0L)
                .name(writeItemRequest.getItemName())
                .description(writeItemRequest.getDescription())
                .brandName(writeItemRequest.getItemBrandName())
                .seller(seller2)
                .build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        final Boolean isSeller = sellerService.sellerCheck(item.getId(), user);

        assertThat(isSeller).isFalse();
    }

    @Test
    void whenIsAnsweredTrue_ThrowAlreadyExistsException_qnaAnswerRegister() {
        Qna qna1 = Qna.builder()
                .id(0L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user)
                .isEmailNotification(true)
                .isSecret(false)
                .question("질문")
                .answer("답변")
                .isAnswered(true)
                .build();

        QnaAnswerRequest qnaAnswerRequest = QnaAnswerRequest.builder()
                .qnaId(0L)
                .answer("답변")
                .build();

        when(qnaRepository.findById(any())).thenReturn(Optional.ofNullable(qna1));

        assertThrows(AlreadyExistsException.class, () -> sellerService.qnaAnswerRegister(qnaAnswerRequest, user));
    }

    @Test
    void whenIsAnsweredFalse_AnswerWillBeSaved_qnaAnswerRegister() {
        Qna qna2 = Qna.builder()
                .id(1L)
                .qnaCategory(QnaCategory.ABOUT_DELIVERY)
                .item(item)
                .user(user)
                .isEmailNotification(true)
                .isSecret(true)
                .question("질문")
                .isAnswered(false)
                .build();

        QnaNotification qnaNotification = QnaNotification.builder()
                .qna(qna2)
                .notificationType(NotificationType.QNA_TO_SELLER)
                .receiver(user)
                .build();

        QnaAnswerRequest qnaAnswerRequest = QnaAnswerRequest.builder()
                .qnaId(0L)
                .answer("답변")
                .build();

        when(qnaRepository.findById(any())).thenReturn(Optional.ofNullable(qna2));
        when(notificationRepository.findByQnaId(any())).thenReturn(qnaNotification);
        doReturn(true).when(sellerService).sellerCheck(anyLong(), any(User.class));

        sellerService.qnaAnswerRegister(qnaAnswerRequest, user);

        verify(meNotificationRepository).save(any());
    }

    @Test
    void temporarySave() {
        OptionValue optionValue = OptionValue.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(true)
                .optionOriginalPrice(5000L)
                .optionSalePrice(3000L)
                .optionStock(5)
                .build();
        OptionValue optionValue2 = OptionValue.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(false)
                .optionOriginalPrice(5000L)
                .optionSalePrice(3000L)
                .optionStock(5)
                .build();
        List<OptionValue> optionValues = new ArrayList<>();
        optionValues.add(optionValue);
        optionValues.add(optionValue2);

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));

        sellerService.temporarySave(apiWriteItemRequest, optionValues, user);

        verify(itemTemporaryRepository, times(2)).save(any());
    }

    @Test
    void retrieveItemTemporaries() {
        ItemTemporary itemTemporary = ItemTemporary.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(false)
                .originalPrice(5000L)
                .salePrice(3000L)
                .stock(5)
                .brandName("나이키")
                .name("상품명")
                .description("상품 설명")
                .seller(seller)
                .build();
        ItemTemporary itemTemporary2 = ItemTemporary.builder()
                .option1("색깔")
                .option2("빨강")
                .isOptionMainItem(true)
                .originalPrice(5000L)
                .salePrice(3000L)
                .stock(5)
                .brandName("나이키")
                .name("상품명2")
                .description("상품 설명2")
                .seller(seller)
                .build();
        List<ItemTemporary> itemTemporaries = new ArrayList<>();
        itemTemporaries.add(itemTemporary);
        itemTemporaries.add(itemTemporary2);

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(itemTemporaryRepository.findAllBySellerId(any())).thenReturn(itemTemporaries);

        final List<ItemTemporaryResponse> itemTemporaryResponses = sellerService.retrieveItemTemporaries(user);

        assertThat(itemTemporaryResponses.size()).isEqualTo(2);
    }

    @Test
    void allFalse_ThrowNotValidException_sellerAgreeCheck() {
        SellerAgreeRequest sellerAgreeRequest = new SellerAgreeRequest(false, false);

        assertThrows(NotValidException.class, () -> sellerService.sellerAgreeCheck(sellerAgreeRequest, user));
    }

    @Test
    void isSellerAgreeFalse_ThrowNotValidException_sellerAgreeCheck() {
        SellerAgreeRequest sellerAgreeRequest = new SellerAgreeRequest(true, false);

        assertThrows(NotValidException.class, () -> sellerService.sellerAgreeCheck(sellerAgreeRequest, user));
    }

    @Test
    void isLawAgreeFalse_ThrowNotValidException_sellerAgreeCheck() {
        SellerAgreeRequest sellerAgreeRequest = new SellerAgreeRequest(false, true);

        assertThrows(NotValidException.class, () -> sellerService.sellerAgreeCheck(sellerAgreeRequest, user));
    }

    @Test
    void allTrue_sellerAgreeCheck() {
        SellerAgreeRequest sellerAgreeRequest = new SellerAgreeRequest(true, true);

        final Boolean agreeCheck = sellerService.sellerAgreeCheck(sellerAgreeRequest, user);

        verify(sellerRepository).save(any());
        assertThat(agreeCheck).isTrue();
    }

    @Test
    void itemDelete() {

        doReturn(true).when(sellerService).sellerCheck(anyLong(), any(User.class));

        sellerService.itemDelete(user, 0L);

        verify(itemRepository).deleteById(any());
        verify(cartRepository).deleteByUserIdAndItemId(any(), any());
    }

    @Test
    void whenCompanyNameExists_AndNotSellerCompanyName_ThrowException_sellerDefaultSettingSave() {
        Seller seller = Seller.builder()
                .companyName("회사명1")
                .build();

        SellerDefaultSettingsRequest sellerDefaultSettingsRequest = SellerDefaultSettingsRequest.builder()
                .companyName("회사명")
                .shippingFeeDefault(3000)
                .contactNumber("02-1234-1234")
                .defaultDeliveryCompany("대한통운")
                .shippingFeeFreePolicy(50000)
                .returnShippingFeeDefault(3000)
                .itemReleaseAddress("서울 종로")
                .itemReleaseDetailAddress("어딘가")
                .itemReleaseZipcode("02020")
                .build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(sellerRepository.existsByCompanyName(any())).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> sellerService.sellerDefaultSettingSave(sellerDefaultSettingsRequest, user));
    }

    @Test
    void whenCompanyNameExists_AndSameAsSellerCompanyName_sellerDefaultSettingSave() {
        Seller seller = Seller.builder()
                .companyName("회사명")
                .build();

        SellerDefaultSettingsRequest sellerDefaultSettingsRequest = SellerDefaultSettingsRequest.builder()
                .companyName("회사명")
                .shippingFeeDefault(3000)
                .contactNumber("02-1234-1234")
                .defaultDeliveryCompany("대한통운")
                .shippingFeeFreePolicy(50000)
                .returnShippingFeeDefault(3000)
                .itemReleaseAddress("서울 종로")
                .itemReleaseDetailAddress("어딘가")
                .itemReleaseZipcode("02020")
                .build();
        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(sellerRepository.existsByCompanyName(any())).thenReturn(true);

        assert seller != null;
        assertThat(seller.getContactNumber()).isNull();

        sellerService.sellerDefaultSettingSave(sellerDefaultSettingsRequest, user);

        assertThat(seller.getContactNumber()).isEqualTo("02-1234-1234");
    }

    @Test
    void sellerSettingsWillBeSaved_sellerDefaultSettingSave() {
        SellerDefaultSettingsRequest sellerDefaultSettingsRequest = SellerDefaultSettingsRequest.builder()
                .companyName("회사명")
                .shippingFeeDefault(3000)
                .contactNumber("02-1234-1234")
                .defaultDeliveryCompany("대한통운")
                .shippingFeeFreePolicy(50000)
                .returnShippingFeeDefault(3000)
                .itemReleaseAddress("서울 종로")
                .itemReleaseDetailAddress("어딘가")
                .itemReleaseZipcode("02020")
                .build();
        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));

        assertThat(seller.getContactNumber()).isNull();

        sellerService.sellerDefaultSettingSave(sellerDefaultSettingsRequest, user);

        assertThat(seller.getContactNumber()).isEqualTo("02-1234-1234");
    }

    @Test
    void sellerDefaultSettings() {
        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));

        final SellerDefaultSettingsResponse sellerDefaultSettingsResponse = sellerService.sellerDefaultSettings(user);

        assertThat(sellerDefaultSettingsResponse).isNotNull();
    }

    @Test
    void hasCompanyName_ReturnTrue_sellerDefaultSettingCheck() {
        Seller seller = Seller.builder()
                .companyName("회사명")
                .build();
        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));

        final boolean check = sellerService.sellerDefaultSettingCheck(user);

        assertThat(check).isTrue();
    }

    @Test
    void companyNameIsNull_ReturnFalse_sellerDefaultSettingCheck() {
        Seller seller = Seller.builder()
                .build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));

        final boolean check = sellerService.sellerDefaultSettingCheck(user);

        assertThat(check).isFalse();
    }

    @Test
    void getSellerRecentOrders() {
        Payment payment = Payment.builder()
                .pg(Pg.TOSSPAY)
                .payMethod(PayMethod.CARD)
                .build();
        Order order = Order.builder()
                .payment(payment)
                .build();
        PaymentPerSeller paymentPerSeller1 = PaymentPerSeller.builder()
                .order(order)
                .payment(payment)
                .seller(seller)
                .itemShippingFeePerSeller(3000)
                .itemTotalQuantityPerSeller(20)
                .itemTotalPricePerSeller(30000L)
                .build();
        PaymentPerSeller paymentPerSeller2 = PaymentPerSeller.builder()
                .order(order)
                .payment(payment)
                .seller(seller)
                .itemShippingFeePerSeller(3000)
                .itemTotalQuantityPerSeller(20)
                .itemTotalPricePerSeller(30000L)
                .build();
        List<PaymentPerSeller> paymentPerSellers = new ArrayList<>();
        paymentPerSellers.add(paymentPerSeller1);
        paymentPerSellers.add(paymentPerSeller2);

        OrderItem orderItem1 = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .seller(seller)
                .order(order)
                .mainImageId(0L)
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .seller(seller)
                .order(order)
                .mainImageId(0L)
                .build();
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem1);
        orderItemList.add(orderItem2);

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(paymentPerSellerRepository.findBySellerIdOrderByCreatedDateDesc(any(), any())).thenReturn(paymentPerSellers);
        when(orderItemRepository.findByOrderIdAndSellerId(any(), any())).thenReturn(orderItemList);

        final List<RecentPaymentPerSellerResponse> sellerRecentOrders = sellerService.getSellerRecentOrders(user, Pageable.unpaged());

        assertThat(sellerRecentOrders.size()).isEqualTo(2);
        assertThat(sellerRecentOrders.get(0).getPaymentPerSellerResponse()).isNotNull();
        assertThat(sellerRecentOrders.get(0).getRecentPaymentPerSellerSimpleResponse()).isNotNull();
    }

    @Test
    void showOrderDetail() {
        Payment payment = Payment.builder()
                .pg(Pg.TOSSPAY)
                .payMethod(PayMethod.CARD)
                .receiverInfo(ReceiverInfo.builder().build())
                .build();

        Order order = Order.builder()
                .payment(payment)
                .build();
        OrderDelivery delivering = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.DELIVERING)
                .build();
        OrderDelivery paymentDone = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                .build();

        OrderItem orderItem1 = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .seller(seller)
                .order(order)
                .orderDelivery(delivering)
                .mainImageId(0L)
                .build();
        OrderItem orderItem2 = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .seller(seller)
                .orderDelivery(paymentDone)
                .order(order)
                .mainImageId(0L)
                .build();
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem1);
        orderItemList.add(orderItem2);
        PaymentPerSeller paymentPerSeller = PaymentPerSeller.builder()
                .order(order)
                .payment(payment)
                .seller(seller)
                .itemShippingFeePerSeller(3000)
                .itemTotalQuantityPerSeller(20)
                .itemTotalPricePerSeller(30000L)
                .build();

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(orderRepository.findById(any())).thenReturn(Optional.ofNullable(order));
        when(orderItemRepository.findByOrderIdAndSellerId(any(), any())).thenReturn(orderItemList);
        when(paymentPerSellerRepository.findByOrderIdAndSellerId(any(), any())).thenReturn(paymentPerSeller);

        final OrderDetailResponse orderDetailResponse = sellerService.showOrderDetail(0L, user);

        assertThat(orderDetailResponse).isNotNull();
        assertThat(orderDetailResponse.getOrderItemResponses()).isNotNull();
        assertThat(orderDetailResponse.getOrderItemResponses().get(0).getIsTrackingStarted()).isTrue();
        assertThat(orderDetailResponse.getOrderItemResponses().get(1).getIsTrackingStarted()).isFalse();
        assertThat(orderDetailResponse.getPaymentDetailResponse()).isNotNull();
        assertThat(orderDetailResponse.getPaymentDetailResponse().getIsAllItemTrackingNumberIssued()).isFalse();
    }

    @Test
    void getSellerBalance() {
        Seller seller = Seller.builder()
                .bankAccount(3000L)
                .build();
        SellerBankAccountHistory sellerBankAccountHistory1 = SellerBankAccountHistory.builder()
                .seller(seller)
                .transactionMoney(3000L)
                .transactionType(TransactionType.DEPOSIT)
                .build();
        SellerBankAccountHistory sellerBankAccountHistory2 = SellerBankAccountHistory.builder()
                .seller(seller)
                .transactionMoney(3000L)
                .transactionType(TransactionType.WITHDRAWAL)
                .build();
        List<SellerBankAccountHistory> sellerBankAccountHistories = new ArrayList<>();
        sellerBankAccountHistories.add(sellerBankAccountHistory1);
        sellerBankAccountHistories.add(sellerBankAccountHistory2);

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(sellerBankAccountHistoryRepository.findTop20BySellerIdOrderByCreatedDateDesc(any())).thenReturn(sellerBankAccountHistories);

        final SellerBankResponse sellerBalance = sellerService.getSellerBalance(user);

        assertThat(sellerBalance.getBankAccount()).isEqualTo(3000);
        assertThat(sellerBalance.getSellerBankAccountHistoryResponses().size()).isEqualTo(2);
    }

    @Test
    void getStatisticsByDay() {
        Payment payment = Payment.builder()
                .pg(Pg.TOSSPAY)
                .payMethod(PayMethod.CARD)
                .receiverInfo(ReceiverInfo.builder().build())
                .build();

        Order order = Order.builder()
                .payment(payment)
                .build();

        PaymentPerSeller paymentPerSeller1 = PaymentPerSeller.builder()
                .order(order)
                .payment(payment)
                .seller(seller)
                .itemShippingFeePerSeller(3000)
                .itemTotalQuantityPerSeller(20)
                .itemTotalPricePerSeller(30000L)
                .build();
        PaymentPerSeller paymentPerSeller2 = PaymentPerSeller.builder()
                .order(order)
                .payment(payment)
                .seller(seller)
                .itemShippingFeePerSeller(3000)
                .itemTotalQuantityPerSeller(20)
                .itemTotalPricePerSeller(30000L)
                .build();
        List<PaymentPerSeller> paymentPerSellers = new ArrayList<>();
        paymentPerSellers.add(paymentPerSeller1);
        paymentPerSellers.add(paymentPerSeller2);
        paymentPerSellers.add(paymentPerSeller1);
        paymentPerSellers.add(paymentPerSeller2);
        paymentPerSellers.add(paymentPerSeller1);
        paymentPerSellers.add(paymentPerSeller2);
        paymentPerSellers.add(paymentPerSeller1);
        paymentPerSellers.add(paymentPerSeller2);
        paymentPerSellers.add(paymentPerSeller1);
        paymentPerSellers.add(paymentPerSeller2);

        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));
        when(paymentPerSellerRepository.findBySellerIdAndCreatedDateBetween(any(), any(), any())).thenReturn(paymentPerSellers);
        final List<StatisticsResponse> statisticsResponses = sellerService.getStatisticsByDay(user);

        assertThat(statisticsResponses).isNotEmpty();
        assertThat(statisticsResponses.size()).isLessThanOrEqualTo(7);
        assertThat(statisticsResponses.get(0).getDate()).isBefore(LocalDateTime.now());
        assertThat(statisticsResponses.get(0).getTotalOrderPerDay()).isEqualTo(10);
        assertThat(statisticsResponses.get(0).getTotalPricePerDay()).isEqualTo(300000);
        assertThat(statisticsResponses.get(0).getTotalQuantityPerDay()).isEqualTo(200);
    }

    @Test
    void getItemRecentReviews() {
        when(sellerRepository.findByUserIdAndIsActivatedTrue(any())).thenReturn(Optional.ofNullable(seller));

        sellerService.getItemRecentReviews(user, Pageable.unpaged());
    }
}