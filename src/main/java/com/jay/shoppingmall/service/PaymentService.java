package com.jay.shoppingmall.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jay.shoppingmall.config.ExcludeFromJacocoGeneratedReport;
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
import com.jay.shoppingmall.domain.notification.Notification;
import com.jay.shoppingmall.domain.notification.item_notification.ItemNotification;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        if (paymentPerSeller.getIsMoneyTransferredToSeller()) {
            log.info("Transaction tried but was transacted already. companyName = '{}', orderItemId = '{}'", seller.getCompanyName(), orderItem.getId());
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
        log.info("Money transaction done. companyName = '{}', money = '{}'", seller.getCompanyName(), moneyToSellerBankAccount);
    }

    public PaymentDetailResponse paymentTotal(String imp_uid, final String merchant_uid, User user) throws IOException {
        Payment paymentRecord = validationPayment(imp_uid, merchant_uid);

        Order order = getOrder(user, paymentRecord);

        final List<Cart> carts = setPaymentPerSellerFromCarts(user, paymentRecord, order);

        moveCartToOrderItem(order, carts);

        cartsStockManipulation(carts);

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

    private List<Cart> setPaymentPerSellerFromCarts(final User user, final Payment paymentRecord, final Order order) {
        final List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user);

        final List<Seller> sellers = carts.stream()
                .map(Cart::getItem)
                .map(Item::getSeller)
                .distinct().collect(Collectors.toList());

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
                    .build();

            paymentPerSellerRepository.save(paymentPerSeller);
        }
        return carts;
    }

    private Order getOrder(final User user, final Payment paymentRecord) {
        Order order = Order.builder()
                .user(user)
                .payment(paymentRecord)
                .build();
        orderRepository.save(order);

        return order;
    }

    private void cartsStockManipulation(final List<Cart> carts) {
        for (Cart cart : carts) {
            final ItemStock itemStock = cart.getItemStock();
            final Integer stockMinusQuantity = itemStock.stockMinusQuantity(cart.getQuantity());

            //재고 변경 기록 저장
            ItemStockHistory itemStockHistory = ItemStockHistory.builder()
                    .itemStock(itemStock)
                    .stock(stockMinusQuantity)
                    .stockChangedDate(LocalDateTime.now())
                    .build();
            itemStockHistoryRepository.save(itemStockHistory);

            if (cart.getStockNow() == 0) {
                //품절일때 로직 작성
                //seller에게 메시지 보내기
            }
        }
    }

    private void moveCartToOrderItem(final Order order, final List<Cart> carts) {
        for (Cart cart : carts) {
            OrderDelivery orderDelivery = OrderDelivery.builder()
                    .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                    .build();
            orderDeliveryRepository.save(orderDelivery);

            Item item = cart.getItem();
            ItemOption itemOption = cart.getItemOption();
            Seller seller = cart.getItemSeller();
            final Long mainImageId = imageRepository.findByImageRelationAndForeignId(ImageRelation.ITEM_MAIN, cart.getItem().getId()).getId();
            OrderItem orderItem = OrderItem.builder()
                    .mainImageId(mainImageId)
                    .itemName(item.getName())
                    .itemOption1(itemOption.getOption1())
                    .itemOption2(itemOption.getOption2())
                    .sellerCompanyName(seller.getCompanyName())
                    .priceAtPurchase(cart.getPriceNow())
                    .quantity(cart.getQuantity())
                    .itemOption(cart.getItemOption())
                    .order(order)
                    .item(cart.getItem())
                    .seller(seller)
                    .orderDelivery(orderDelivery)
                    .build();

            orderItemRepository.save(orderItem);

            cartRepository.delete(cart);
        }
    }

    /**
     * PG사에 의해 실제 결제된 금액과 DB에 저장된 금액을 비교합니다.
     * 금액이 다른 경우 결제가 실패하며 조작 시도 여부가 DB에 저장됩니다.
     * @param imp_uid
     * @param merchant_uid
     * @return
     * @throws IOException
     */
    private Payment validationPayment(final String imp_uid, final String merchant_uid) throws IOException {
        String accessToken = getAccessToken();

        //PG사에 의해 실제로 결제된 금액
        Long amountByPg = getPaymentInfoByToken(imp_uid, accessToken);

        //결제 금액과 레코드 검증.
        Payment paymentRecord = paymentRepository.findByMerchantUid(merchant_uid)
                .orElseThrow(() -> new PaymentFailedException("결제 정보가 없습니다"));

        if (!amountByPg.equals(paymentRecord.getAmount())) {
            log.warn("Payment manipulation. imp_uid = '{}', merchant_uid = '{}', expectedMoney = '{}', actualMoney = '{}'",
                    imp_uid, merchant_uid, paymentRecord.getAmount(), amountByPg);
            paymentRecord.isAmountManipulatedTrue();
            throw new PaymentFailedException("결제 정보가 올바르지 않습니다.");
        }

        //검증 완료
        paymentRecord.isValidatedTrue();
        return paymentRecord;
    }

    /**
     * 실제 결제가 되기 전에 미리 DB에 결제 정보를 저장합니다. 이 값은 후에 실제 결제 금액과 비교 대조됩니다.
     *
     * @param paymentRequest
     * @param user
     * @return
     */
    public PaymentResponse paymentRecordGenerateBeforePg(final PaymentRequest paymentRequest, final User user) {
        List<Cart> carts = cartRepository.findByUserAndIsSelectedTrue(user);
        String merchantUid = merchantUidGenerator.generateMerchantUid();

        long totalQuantity = carts.stream().mapToLong(Cart::getQuantity).sum();
        Long mostExpensivePrice = -1L;
        String mostExpensiveOne = "";
        for (Cart cart : carts) {
            if (cart.getPriceNow() > mostExpensivePrice) {
                mostExpensivePrice = cart.getPriceNow();
                mostExpensiveOne = cart.getItem().getName();
            }
        }
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

        if (cartPriceTotal == 0 || mostExpensivePrice == -1) {
            log.warn("payment failed. userId = '{}'", user.getId());
            throw new PaymentFailedException("가격 오류가 발생하였습니다.");
        }

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

        log.info("Payment record saved before pg. email = '{}', amount = '{}', merchantUid = '{}', name = '{}'", user.getEmail(), amount, merchantUid, name);
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
     * iamport사에 POST 요청하여 access token을 받아옵니다.
     * access token은 getPaymentInfoByToken에서 실제 결제 정보를 받아오는데 사용됩니다.
     * @return access_token
     * @throws IOException
     */
    @ExcludeFromJacocoGeneratedReport
    String getAccessToken() throws IOException {

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
        String token = gson.fromJson(response, Map.class).get("access_token").toString();

        br.close();
        conn.disconnect();

        return token;
    }

    /**
     * iamport사에 GET 요청하여 실제 결제된 금액을 받아와서 반환합니다.
     * @param imp_uid
     * @param access_token
     * @return 실제 결제된 총액
     * @throws IOException
     */
    @ExcludeFromJacocoGeneratedReport
    Long getPaymentInfoByToken(String imp_uid, String access_token) throws IOException {
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

    @ExcludeFromJacocoGeneratedReport
    @ToString
    @Getter
    private static class Response {
        private PaymentInfo response;
    }
    @ExcludeFromJacocoGeneratedReport
    @ToString
    @Getter
    private static class PaymentInfo {
        private Long amount;
    }


}
