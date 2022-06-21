package com.jay.shoppingmall.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jay.shoppingmall.domain.cart.Cart;
import com.jay.shoppingmall.domain.cart.CartRepository;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.item.item_option.ItemOptionRepository;
import com.jay.shoppingmall.domain.item.item_price.ItemPrice;
import com.jay.shoppingmall.domain.item.item_stock.ItemStock;
import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistory;
import com.jay.shoppingmall.domain.item.item_stock.item_stock_history.ItemStockHistoryRepository;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDeliveryRepository;
import com.jay.shoppingmall.domain.payment.model.MerchantUidGenerator;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.PaymentRepository;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSeller;
import com.jay.shoppingmall.domain.payment.payment_per_seller.PaymentPerSellerRepository;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistory;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.SellerBankAccountHistoryRepository;
import com.jay.shoppingmall.domain.seller.seller_bank_account_history.TransactionType;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.PaymentRequest;
import com.jay.shoppingmall.dto.response.cart.CartPricePerSellerResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentResponse;
import com.jay.shoppingmall.dto.response.order.payment.PaymentDetailResponse;
import com.jay.shoppingmall.exception.exceptions.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ImageRepository imageRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final ItemStockHistoryRepository itemStockHistoryRepository;
    private final PaymentPerSellerRepository paymentPerSellerRepository;
    private final SellerBankAccountHistoryRepository sellerBankAccountHistoryRepository;

    private final CartService cartService;
    private final MerchantUidGenerator merchantUidGenerator;

    public void moneyTransactionToSeller(final OrderItem orderItem) {
        orderItem.getOrderDelivery().paymentDone();

        final Seller seller = orderItem.getSeller();

        final PaymentPerSeller paymentPerSeller = paymentPerSellerRepository.findByOrderIdAndSellerId(orderItem.getOrder().getId(), seller.getId());
        if (paymentPerSeller.getIsMoneyTransferredToSeller() != null && paymentPerSeller.getIsMoneyTransferredToSeller()) {
            throw new MoneyTransactionException("이미 정산이 완료된 주문입니다");
        }

        final Long moneyToSellerBankAccount = paymentPerSeller.getItemTotalPricePerSeller();
        SellerBankAccountHistory sellerBankAccountHistory = SellerBankAccountHistory.builder()
                .transactionMoney(moneyToSellerBankAccount)
                .transactionType(TransactionType.DEPOSIT)
                .seller(seller)
                .build();
        sellerBankAccountHistoryRepository.save(sellerBankAccountHistory);

        seller.sellerBankAccountUp(moneyToSellerBankAccount);
        paymentPerSeller.paymentToSellerTrue();
    }


    public PaymentDetailResponse paymentTotal(String imp_uid, final String merchant_uid, User user) throws IOException {
        String accessToken = getAccessToken();

        //PG사에 의해 실제로 결제된 금액
        Long amountByPg = getPaymentInfoByToken(imp_uid, accessToken);

        //결제 금액과 레코드 검증.
        Payment paymentRecord = paymentRepository.findByMerchantUid(merchant_uid)
                .orElseThrow(() -> new PaymentFailedException("결제 정보가 없습니다"));

        if (!amountByPg.equals(paymentRecord.getAmount())) {
            paymentRecord.isAmountManipulatedTrue();
            throw new PaymentFailedException("결제 정보가 올바르지 않습니다.");
        }

        //검증 완료
        paymentRecord.isValidatedTrue();

        Order order = Order.builder()
                .user(user)
                .payment(paymentRecord)
                .build();
        orderRepository.save(order);

        final List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user);
//                .orElseThrow(() -> new CartEmptyException("장바구니의 값이 올바르지 않습니다"));

        final List<Seller> sellers = carts.stream().map(Cart::getItem).map(Item::getSeller).distinct().collect(Collectors.toList());
        //판매자별 개별 결제 정보 저장

        for (Seller seller : sellers) {

            final CartPricePerSellerResponse cartPricePerSellerResponse = cartService.cartPricePerSeller(user, seller);

            PaymentPerSeller paymentPerSeller = PaymentPerSeller
                    .builder()
                    .itemTotalPricePerSeller(cartPricePerSellerResponse.getItemTotalPricePerSeller())
                    .itemTotalQuantityPerSeller(cartPricePerSellerResponse.getItemTotalQuantityPerSeller())
                    .itemShippingFeePerSeller(cartPricePerSellerResponse.getItemShippingFeePerSeller())
                    .seller(seller)
                    .payment(paymentRecord)
                    .order(order)
//                    .orderItems(orderItemsPerSeller)
                    .build();

            paymentPerSellerRepository.save(paymentPerSeller);
        }


        //상품 주문 테이블로 정보 옮기고 장바구니 비우기
        for (Cart cart : carts) {
            OrderDelivery orderDelivery = OrderDelivery.builder()
                    .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                    .build();
            orderDeliveryRepository.save(orderDelivery);

            //대표 사진 관리에 대한 주체를 OrderItem에게 넘김. 상품이 삭제될 수 있으므로.
            //나중에 썸네일로 로직바꿀 것.
            final Long mainImageId = imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, cart.getItem().getId()).getId();
            OrderItem orderItem = OrderItem.builder()
                    .mainImageId(mainImageId)
                    .order(order)
                    .priceAtPurchase(cart.getItemOption().getItemPrice().getPriceNow())
                    .quantity(cart.getQuantity())
                    .itemOption(cart.getItemOption())
                    .item(cart.getItem())
                    .seller(cart.getItem().getSeller())
                    .orderDelivery(orderDelivery)
                    .build();

            orderItemRepository.save(orderItem);

            cartRepository.delete(cart);
        }

        //재고 관리
        for (Cart cart : carts) {
            final ItemStock itemStock = cart.getItemOption().getItemStock();
            final Integer stockMinusQuantity = itemStock.stockMinusQuantity(cart.getQuantity());

            //재고 변경 기록 저장
            ItemStockHistory itemStockHistory = ItemStockHistory.builder()
                    .itemStock(itemStock)
                    .stock(stockMinusQuantity)
                    .stockChangedDate(LocalDateTime.now())
                    .build();
            itemStockHistoryRepository.save(itemStockHistory);

            if (cart.getItemOption().getItemStock().getStock() == 0) {
                //품절일때 로직 작성
                //seller에게 메시지 보내기
            }
        }

        return PaymentDetailResponse.builder()
                .pg(paymentRecord.getPg().getName())
                .payMethod(paymentRecord.getPayMethod().getName())
                .amount(paymentRecord.getAmount())
                .buyerAddr(paymentRecord.getBuyerAddr())
                .buyerEmail(paymentRecord.getBuyerEmail())
                .buyerName(paymentRecord.getBuyerName())
                .buyerPostcode(paymentRecord.getBuyerPostcode())
                .buyerTel(paymentRecord.getBuyerTel())
                .merchantUid(paymentRecord.getMerchantUid())
                .build();
    }

    public PaymentResponse paymentRecordGenerateBeforePg(final PaymentRequest paymentRequest, final User user) {
        List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user);
//                .orElseThrow(() -> new UserNotFoundException("잘못된 접근입니다"));
        String merchantUid = merchantUidGenerator.generateMerchantUid();

        long totalQuantity = carts.stream().mapToLong(Cart::getQuantity).sum();
        Long mostExpensiveOnePriceId = carts.stream().map(Cart::getItemOption).map(ItemOption::getItemPrice).max(Comparator.comparingLong(ItemPrice::getPriceNow)).map(ItemPrice::getId)
                .orElseThrow(()-> new ItemNotFoundException("상품이 존재하지 않습니다"));
        String mostExpensiveOne = itemOptionRepository.findByItemPriceId(mostExpensiveOnePriceId).getItem().getName();
        String name = totalQuantity == 1 ? mostExpensiveOne : mostExpensiveOne + " 외 " + (totalQuantity - 1) + "건";

        long cartPriceTotal = 0;
        int shippingFeeTotal = 0;

        final List<Seller> sellers = carts.stream().map(Cart::getItem).map(Item::getSeller).distinct().collect(Collectors.toList());
        for (Seller seller : sellers) {
            final CartPricePerSellerResponse cartPricePerSellerResponse = cartService.cartPricePerSeller(user, seller);
            cartPriceTotal += cartPricePerSellerResponse.getItemTotalPricePerSeller();
            shippingFeeTotal += cartPricePerSellerResponse.getItemShippingFeePerSeller();
        }
        long amount = cartPriceTotal + shippingFeeTotal;

        //유효성 검사 로직 더 작성할 것.

        ReceiverInfo receiverInfo = ReceiverInfo.builder()
                .receiverAddress(paymentRequest.getBuyerAddr())
                .receiverEmail(paymentRequest.getBuyerEmail())
                .receiverName(paymentRequest.getBuyerName())
                .receiverPhoneNumber(paymentRequest.getBuyerTel())
                .receiverPostcode(paymentRequest.getBuyerPostcode())
                .build();

        Payment payment = Payment.builder()
                .user(user)
                .name(name)
                .amount(amount)
                .merchantUid(merchantUid)
                .payMethod(paymentRequest.getPayMethod())
                .pg(paymentRequest.getPg())
                .buyerAddr(user.getAddress().getFullAddress())
                .buyerPostcode(user.getAddress().getZipcode())
                .buyerName(user.getName().getFullName())
                .buyerTel(user.getPhoneNumber().getFullNumber())
                .buyerEmail(user.getEmail())
                .receiverInfo(receiverInfo)
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .amount(amount)
                .merchantUid(merchantUid)
                .build();
    }

    @Value("${imp_key}")
    private String imp_key;

    @Value("${imp_secret}")
    private String imp_secret;

    /**
     *
     * @return access_token
     * @throws IOException
     */
    private String getAccessToken() throws IOException {

        HttpsURLConnection conn = null;
        URL url = new URL("https://api.iamport.kr/users/getToken");
        conn = (HttpsURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        JsonObject json = new JsonObject();

        json.addProperty("imp_key", imp_key);
        json.addProperty("imp_secret", imp_secret);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

        bw.write(json.toString());
        bw.flush();
        bw.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

        Gson gson = new Gson();
        String response = gson.fromJson(br.readLine(), Map.class).get("response").toString();
        System.out.println("response : " + response);
        String token = gson.fromJson(response, Map.class).get("access_token").toString();

        br.close();
        conn.disconnect();

        return token;
    }

    /**
     *
     * @param imp_uid
     * @param access_token
     * @return 총액
     * @throws IOException
     */
    private Long getPaymentInfoByToken(String imp_uid, String access_token) throws IOException {
        HttpsURLConnection connection = null;
        URL url = new URL("https://api.iamport.kr/payments/" + imp_uid);
        connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", access_token);
        connection.setDoOutput(true);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

        Gson gson = new Gson();
        Response response = gson.fromJson(bufferedReader.readLine(), Response.class);

        bufferedReader.close();
        connection.disconnect();

        return response.getResponse().getAmount();
    }

    @ToString
    @Getter
    private static class Response {
        private PaymentInfo response;
    }
    @ToString
    @Getter
    private static class PaymentInfo {
        private Long amount;
    }


}
