//package com.jay.shoppingmall.domain.payment.payment_per_seller;
//
//import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
//import com.jay.shoppingmall.domain.item.Item;
//import com.jay.shoppingmall.domain.item.ItemRepository;
//import com.jay.shoppingmall.domain.item.item_option.ItemOption;
//import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
//import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
//import com.jay.shoppingmall.domain.item.item_price.ItemPriceRepository;
//import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
//import com.jay.shoppingmall.domain.item.item_stock.ItemStockRepository;
//import com.jay.shoppingmall.domain.order.Order;
//import com.jay.shoppingmall.domain.order.OrderRepository;
//import com.jay.shoppingmall.domain.payment.Payment;
//import com.jay.shoppingmall.domain.payment.PaymentRepository;
//import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
//import com.jay.shoppingmall.domain.seller.Seller;
//import com.jay.shoppingmall.domain.seller.SellerRepository;
//import com.jay.shoppingmall.domain.user.Role;
//import com.jay.shoppingmall.domain.user.User;
//import com.jay.shoppingmall.domain.user.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.annotation.DirtiesContext;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
//class PaymentPerSellerRepositoryTest {
//
//    @Autowired
//    PaymentPerSellerRepository paymentPerSellerRepository;
//    @Autowired
//    ItemOptionRepository itemOptionRepository;
//    @Autowired
//    ItemRepository itemRepository;
//    @Autowired
//    ItemStockRepository itemStockRepository;
//    @Autowired
//    ItemPriceRepository itemPriceRepository;
//    @Autowired
//    SellerRepository sellerRepository;
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    PaymentRepository paymentRepository;
//    @Autowired
//    OrderRepository orderRepository;
//    Item item;
//    ItemPrice itemPrice;
//    Payment payment;
//    private Seller seller;
//    private Order order1;
//    private Order order2;
//    private Order order3;
//
//    @BeforeEach
//    void setUp() {
//        User user = User.builder()
//                .email("qwe@qwe")
//                .role(Role.ROLE_USER)
//                .password(("qweqweqwe1"))
//                .build();
//        userRepository.save(user);
//
//        seller = EntityBuilder.getSeller();
//        sellerRepository.save(seller);
//
//        item = EntityBuilder.getItem();
//        itemRepository.save(item);
//
//        final ItemStock itemStock = EntityBuilder.getItemStock();
//        itemStockRepository.save(itemStock);
//
//        itemPrice = EntityBuilder.getItemPrice();
//        itemPriceRepository.save(itemPrice);
//
//        final ItemOption itemOption = EntityBuilder.getItemOption();
//        itemOptionRepository.save(itemOption);
//
//        final ReceiverInfo receiverInfo = ReceiverInfo.builder()
//                .receiverName("수신자 이름")
//                .receiverAddress("서울시 종로")
//                .receiverEmail("qwerty@email.com")
//                .receiverPhoneNumber("010-1234-1234")
//                .receiverPostcode("12345")
//                .build();
//        payment = Payment.builder()
//                .user(user)
//                .receiverInfo(receiverInfo)
//                .build();
//        paymentRepository.save(payment);
//
//        order1 = Order.builder()
//                .user(user)
//                .payment(payment)
//                .build();
//        order2 = Order.builder()
//                .user(user)
//                .payment(payment)
//                .build();
//        order3 = Order.builder()
//                .user(user)
//                .payment(payment)
//                .build();
//        orderRepository.save(order1);
//        orderRepository.save(order2);
//        orderRepository.save(order3);
//
//        PaymentPerSeller paymentPerSeller1 = PaymentPerSeller.builder()
//                .seller(seller)
//                .itemTotalPricePerSeller(30000L)
//                .itemTotalQuantityPerSeller(30)
//                .itemShippingFeePerSeller(3000)
//                .payment(payment)
//                .order(order1)
//                .build();
//        PaymentPerSeller paymentPerSeller2 = PaymentPerSeller.builder()
//                .seller(seller)
//                .itemTotalPricePerSeller(30000L)
//                .itemTotalQuantityPerSeller(30)
//                .itemShippingFeePerSeller(3000)
//                .payment(payment)
//                .order(order2)
//                .build();
//        PaymentPerSeller paymentPerSeller3 = PaymentPerSeller.builder()
//                .seller(seller)
//                .itemTotalPricePerSeller(30000L)
//                .itemTotalQuantityPerSeller(30)
//                .itemShippingFeePerSeller(3000)
//                .payment(payment)
//                .order(order3)
//                .build();
//        paymentPerSellerRepository.save(paymentPerSeller1);
//        paymentPerSellerRepository.save(paymentPerSeller2);
//        paymentPerSellerRepository.save(paymentPerSeller3);
//    }
//
//    @Test
//    void findByPayment() {
//        final List<PaymentPerSeller> paymentPerSellers = paymentPerSellerRepository.findByPayment(payment);
//
//        assertThat(paymentPerSellers.size()).isEqualTo(3);
//    }
//
//    @Test
//    void findBySellerIdOrderByCreatedDateDesc() {
//        final List<PaymentPerSeller> paymentPerSellers = paymentPerSellerRepository.findBySellerIdOrderByCreatedDateDesc(seller.getId(), Pageable.unpaged());
//
//        assertThat(paymentPerSellers.size()).isEqualTo(3);
//        assertThat(paymentPerSellers.get(0).getCreatedDate()).isAfter(paymentPerSellers.get(1).getCreatedDate());
//        assertThat(paymentPerSellers.get(1).getCreatedDate()).isAfter(paymentPerSellers.get(2).getCreatedDate());
//    }
//
//    @Test
//    void findByOrderIdAndSellerId() {
//        final PaymentPerSeller paymentPerSeller = paymentPerSellerRepository.findByOrderIdAndSellerId(order1.getId(), seller.getId());
//
//        assertThat(paymentPerSeller).isNotNull();
//    }
//
//    @Test
//    void findBySellerIdAndCreatedDateBetween() {
//        final List<PaymentPerSeller> paymentPerSellers = paymentPerSellerRepository.findBySellerIdAndCreatedDateBetween(seller.getId(), LocalDateTime.now().minusDays(1), LocalDateTime.now());
//
//        assertThat(paymentPerSellers.size()).isEqualTo(3);
//    }
//}