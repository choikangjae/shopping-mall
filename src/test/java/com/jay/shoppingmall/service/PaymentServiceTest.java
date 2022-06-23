package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistoryRepository;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDeliveryRepository;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.payment.model.MerchantUidGenerator;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSellerRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistoryRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.exception.exceptions.DeliveryException;
import com.jay.shoppingmall.exception.exceptions.MoneyTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Spy
    @InjectMocks
    PaymentService paymentService;
    @Mock
    PaymentRepository paymentRepository;
    @Mock
    CartRepository cartRepository;
    @Mock
    OrderRepository orderRepository;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    ItemOptionRepository itemOptionRepository;
    @Mock
    OrderDeliveryRepository orderDeliveryRepository;
    @Mock
    ItemStockHistoryRepository itemStockHistoryRepository;
    @Mock
    PaymentPerSellerRepository paymentPerSellerRepository;
    @Mock
    SellerBankAccountHistoryRepository sellerBankAccountHistoryRepository;

    @Mock
    CartService cartService;
    @Mock
    MerchantUidGenerator merchantUidGenerator;


    OrderItem orderItem;
    PaymentPerSeller paymentPerSeller;
    Order order;
    Seller seller;
    @BeforeEach
    void setUp() {
        seller = EntityBuilder.getSeller();

        order = Order.builder()
                .build();
        OrderDelivery orderDelivery = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                .build();

        orderItem = OrderItem.builder()
                .orderDelivery(orderDelivery)
                .order(order)
                .seller(seller)
                .quantity(5)
                .build();

        paymentPerSeller = PaymentPerSeller.builder()
                .itemTotalPricePerSeller(3000L)
                .isMoneyTransferredToSeller(false)
                .build();
    }

    @Test
    void whenIsMoneyTransferredToSellerTrue_ThrowMoneyTransactionException_moneyTransactionToSeller() {
        paymentPerSeller = PaymentPerSeller.builder()
                .itemTotalPricePerSeller(3000L)
                .isMoneyTransferredToSeller(true)
                .build();

        when(paymentPerSellerRepository.findByOrderIdAndSellerId(any(), any())).thenReturn(paymentPerSeller);

        assertThrows(MoneyTransactionException.class, () -> paymentService.moneyTransactionToSeller(orderItem));
    }
    @Test
    void whenOrderStatusIsFinished_ThrowDeliveryException_moneyTransactionToSeller() {
        OrderDelivery orderDelivery = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.FINISHED)
                .build();

        orderItem = OrderItem.builder()
                .orderDelivery(orderDelivery)
                .order(order)
                .seller(seller)
                .quantity(5)
                .build();

        assertThrows(DeliveryException.class, () -> paymentService.moneyTransactionToSeller(orderItem));
    }
    @Test
    void happyPath_moneyTransactionToSeller() {
        final Long bankAccountBefore = seller.getBankAccount();
        when(paymentPerSellerRepository.findByOrderIdAndSellerId(any(), any())).thenReturn(paymentPerSeller);

        assertThat(paymentPerSeller.getIsMoneyTransferredToSeller()).isFalse();

        paymentService.moneyTransactionToSeller(orderItem);

        assertThat(seller.getBankAccount()).isEqualTo(bankAccountBefore + paymentPerSeller.getItemTotalPricePerSeller());
        assertThat(paymentPerSeller.getIsMoneyTransferredToSeller()).isTrue();
        verify(sellerBankAccountHistoryRepository).save(any());
    }

    @Test
    void paymentTotal() throws IOException {

//        Long paidTotal = 10000L;
//        try (MockedStatic<PaymentService> mockedPaymentService = mockStatic(PaymentService.class)) {
//            mockedPaymentService.when(PaymentService::)
//            mockedLocalDateTime.when(LocalDateTime::now).thenReturn(defaultTime);
//        }
//        doReturn(true).when(paymentService).getPaymentInfoByToken(any(), any());

        paymentService.paymentTotal(any(), any(), any());
    }

    @Test
    void paymentRecordGenerateBeforePg() {
    }
}