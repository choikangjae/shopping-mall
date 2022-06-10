package com.jay.shoppingmall.demo;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.response.order.OrderItemResponse;
import com.jay.shoppingmall.exception.exceptions.SellerNotFoundException;
import com.jay.shoppingmall.exception.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DemoService {

    private final SellerRepository sellerRepository;
    private final OrderItemRepository orderItemRepository;

    public List<OrderItemResponse> deliveryChange(final User user, Pageable pageable) {
        final Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자 오류"));
        final List<OrderItem> orderItems = orderItemRepository.findBySellerId(seller.getId(), pageable);

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            final OrderDelivery orderDelivery = orderItem.getOrderDelivery();

            if (!orderDelivery.getDeliveryStatus().equals(DeliveryStatus.DELIVERED)) {
                OrderItemResponse orderItemResponse = OrderItemResponse.builder()
                        .itemName(orderItem.getItem().getName())
                        .sellerCompanyName(orderItem.getSeller().getCompanyName())
                        .orderItemId(orderItem.getId())
                        .itemPrice(orderItem.getPriceAtPurchase())
                        .quantity(orderItem.getQuantity())
                        .deliveryStatus(orderDelivery.getDeliveryStatus().getValue())
                        .build();
                orderItemResponses.add(orderItemResponse);
            }
        }
        return orderItemResponses;
    }
}
