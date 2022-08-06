package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistoryRepository;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDeliveryRepository;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.payment.model.MerchantUidGenerator;
import com.jay.shoppingmall.domain.payment.model.PayMethod;
import com.jay.shoppingmall.domain.payment.model.Pg;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSellerRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistoryRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.response.cart.CartPricePerSellerResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentDetailResponse;
import com.jay.shoppingmall.exception.exceptions.DeliveryException;
import com.jay.shoppingmall.exception.exceptions.MoneyTransactionException;
import com.jay.shoppingmall.exception.exceptions.PaymentFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    User user;
    OrderItem orderItem;
    PaymentPerSeller paymentPerSeller;
    Order order;
    Seller seller;
    Payment payment;

    @BeforeEach
    void setUp() throws IOException {
        user = EntityBuilder.getUser();

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

        payment = Payment.builder()
                .payMethod(PayMethod.CARD)
                .pg(Pg.TOSSPAY)
                .merchantUid("123")
                .amount(10000L)
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
    void whenBeforeAmountIsNotEqualToAfterAmount_ThrowPaymentFailedException_paymentTotal() throws IOException {

        doReturn("토큰").when(paymentService).getAccessToken();
        doReturn(5000L).when(paymentService).getPaymentInfoByToken(any(), any());
        when(paymentRepository.findByMerchantUid(any())).thenReturn(Optional.ofNullable(payment));

        assertThrows(PaymentFailedException.class, () -> paymentService.paymentTotal(any(), any(), any()));
    }
    @Test
    void happyPath_paymentTotal() throws IOException {
        ItemOption itemOption = EntityBuilder.getItemOption();
        ItemOption itemOption2 = EntityBuilder.getItemOption2();

        final Item item = EntityBuilder.getItem();
        final Item item2 = EntityBuilder.getItem2();

        List<Cart> cartList = new ArrayList<>();
        Cart cart = Cart.builder()
                .item(item)
                .quantity(10)
                .itemOption(itemOption)
                .build();
        Cart cart2 = Cart.builder()
                .item(item2)
                .quantity(20)
                .itemOption(itemOption2)
                .build();
        cartList.add(cart);
        cartList.add(cart2);

        CartPricePerSellerResponse cartPricePerSellerResponse = CartPricePerSellerResponse.builder()
                .itemTotalQuantityPerSeller(30)
                .itemTotalPricePerSeller(0L)
                .itemShippingFeePerSeller(3000)
                .build();

        Image image = Image.builder()
                .build();

        doReturn("토큰").when(paymentService).getAccessToken();
        doReturn(10000L).when(paymentService).getPaymentInfoByToken(any(), any());
        when(paymentRepository.findByMerchantUid(any())).thenReturn(Optional.ofNullable(payment));
        when(cartRepository.findByUserAndIsSelectedTrue(any())).thenReturn(cartList);
        when(cartService.cartPricePerSeller(any(), any())).thenReturn(cartPricePerSellerResponse);
        when(imageRepository.findByImageRelationAndForeignId(any(), any())).thenReturn(image);
        final PaymentDetailResponse paymentDetailResponse = paymentService.paymentTotal(any(), any(), any());

        assertThat(paymentDetailResponse.getAmount()).isEqualTo(10000L);
        assertThat(payment.getIsValidated()).isTrue();
        assertThat(payment.getIsAmountManipulated()).isNull();
        verify(orderRepository).save(any());
        verify(orderDeliveryRepository, times(2)).save(any());
    }

    @Test
    void ifTotalPriceIsZero_ThrowPaymentFailedException_paymentRecordGenerateBeforePg() {
        ItemOption itemOption = EntityBuilder.getItemOption();
        ItemOption itemOption2 = EntityBuilder.getItemOption2();

        final Item item = EntityBuilder.getItem();
        final Item item2 = EntityBuilder.getItem2();

        List<Cart> cartList = new ArrayList<>();
        Cart cart = Cart.builder()
                .item(item)
                .quantity(10)
                .itemOption(itemOption)
                .build();
        Cart cart2 = Cart.builder()
                .item(item2)
                .quantity(20)
                .itemOption(itemOption2)
                .build();
        cartList.add(cart);
        cartList.add(cart2);

        CartPricePerSellerResponse cartPricePerSellerResponse = CartPricePerSellerResponse.builder()
                .itemTotalQuantityPerSeller(30)
                .itemTotalPricePerSeller(0L)
                .itemShippingFeePerSeller(3000)
                .build();

        when(cartRepository.findByUserAndIsSelectedTrue(any())).thenReturn(cartList);
        when(merchantUidGenerator.generateMerchantUid()).thenReturn("MerchantUid");
        when(cartService.cartPricePerSeller(any(), any())).thenReturn(cartPricePerSellerResponse);

        assertThrows(PaymentFailedException.class, () -> paymentService.paymentRecordGenerateBeforePg(PaymentRequest.builder().build(), user));
    }
    @Test
    void success_paymentRecordGenerateBeforePg() {
        ItemOption itemOption = EntityBuilder.getItemOption();
        ItemOption itemOption2 = EntityBuilder.getItemOption2();

        final Item item = EntityBuilder.getItem();
        final Item item2 = EntityBuilder.getItem2();

        List<Cart> cartList = new ArrayList<>();
        Cart cart = Cart.builder()
                .item(item)
                .quantity(10)
                .itemOption(itemOption)
                .build();
        Cart cart2 = Cart.builder()
                .item(item2)
                .quantity(20)
                .itemOption(itemOption2)
                .build();
        cartList.add(cart);
        cartList.add(cart2);

        CartPricePerSellerResponse cartPricePerSellerResponse = CartPricePerSellerResponse.builder()
                .itemTotalQuantityPerSeller(30)
                .itemTotalPricePerSeller(50000L)
                .itemShippingFeePerSeller(3000)
                .build();

        when(cartRepository.findByUserAndIsSelectedTrue(any())).thenReturn(cartList);
        when(merchantUidGenerator.generateMerchantUid()).thenReturn("MerchantUid");
        when(cartService.cartPricePerSeller(any(), any())).thenReturn(cartPricePerSellerResponse);

        paymentService.paymentRecordGenerateBeforePg(PaymentRequest.builder().build(), user);

        verify(paymentRepository).save(any());
    }
}