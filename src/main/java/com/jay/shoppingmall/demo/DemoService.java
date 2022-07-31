package com.jay.shoppingmall.demo;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompany;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompanyRepository;
import com.jay.shoppingmall.dto.response.order.OrderItemResponse;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.OrderNotFoundException;
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
    private final VirtualDeliveryCompanyRepository virtualDeliveryCompanyRepository;
    private final OrderRepository orderRepository;

    /**
     * 데모를 위해 만들어진 기능으로 배송 상태를 직접 요청하여 변경할 수 있습니다.
     * 이후에 삭제될 기능입니다.
     * @param user
     * @param pageable
     * @return
     */
    public List<OrderItemResponse> deliveryChange(final User user, Pageable pageable) {
        final Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자 오류"));
        final List<OrderItem> orderItems = orderItemRepository.findBySellerId(seller.getId(), pageable);

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            final VirtualDeliveryCompany virtualDeliveryCompany = virtualDeliveryCompanyRepository.findByOrderItemId(orderItem.getId())
                    .orElseThrow(() -> new ItemNotFoundException("해당 상품이 없음"));

            final String trackingNumber = virtualDeliveryCompany.getTrackingNumber();

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

    /**
     * 데모를 위해 만들어진 기능으로 배송 상태를 직접 완료할 수 있습니다.
     * 이후에 삭제될 기능입니다.
     * @param user
     * @param orderItemId
     */
    public void deliveryDone(final User user, final Long orderItemId) {
        final Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자 오류"));

        final VirtualDeliveryCompany virtualDeliveryCompany = virtualDeliveryCompanyRepository.findByOrderItemId(orderItemId)
                .orElseThrow(() -> new ItemNotFoundException("운송장 조회 실패"));

        final List<VirtualDeliveryCompany> byTrackingNumber = virtualDeliveryCompanyRepository.findByTrackingNumber(virtualDeliveryCompany.getTrackingNumber());

        for (VirtualDeliveryCompany virtualDeliveryCompany1 : byTrackingNumber) {
            virtualDeliveryCompany1.getOrderItem().getOrderDelivery().deliveryDone();
        }
    }
}
