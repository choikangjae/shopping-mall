package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.seller.SellerRepository;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompany;
import com.jay.shoppingmall.domain.virtual_delivery_company.VirtualDeliveryCompanyRepository;
import com.jay.shoppingmall.dto.response.TrackPackageResponse;
import com.jay.shoppingmall.dto.response.VirtualDeliveryResponse;
import com.jay.shoppingmall.dto.response.VirtualDeliveryResponseForAnonymous;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.NotValidException;
import com.jay.shoppingmall.exception.exceptions.SellerNotFoundException;
import com.jay.shoppingmall.service.common.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class VirtualDeliveryService {

    private final OrderItemRepository orderItemRepository;
    private final SellerRepository sellerRepository;
    private final VirtualDeliveryCompanyRepository virtualDeliveryCompanyRepository;

    private final CommonService commonService;

    public void issueTrackingNumber(final List<Long> orderItemIds, User user) {
        final Seller seller = sellerRepository.findByUserIdAndIsActivatedTrue(user.getId())
                .orElseThrow(() -> new SellerNotFoundException("판매자가 아닙니다"));

        String trackingNumber = UUID.randomUUID().toString();

        Set<Order> orderSet = new HashSet<>();
        for (Long orderItemId : orderItemIds) {
            final OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));

            orderItem.getOrderDelivery().deliveryStart();

            final Order order = orderItem.getOrder();
            orderSet.add(order);
        }
        if (orderSet.size() != 1) {
            throw new NotValidException("잘못된 요청입니다");
        }
        final Order order = new ArrayList<>(orderSet).get(0);
        final ReceiverInfo receiverInfo = order.getPayment().getReceiverInfo();

        for (Long orderItemId : orderItemIds) {
            final OrderItem orderItem = orderItemRepository.findById(orderItemId)
                    .orElseThrow(() -> new ItemNotFoundException("상품이 존재하지 않습니다"));

            final VirtualDeliveryCompany virtualDeliveryCompany = VirtualDeliveryCompany.builder()
                    .trackingNumber(trackingNumber)
                    .packageName(order.getPayment().getName())
                    .senderName(seller.getCompanyName())
                    .senderAddress(seller.getItemReleaseAddress().getFullAddress())
                    .senderPostcode(seller.getItemReleaseAddress().getZipcode())
                    .senderPhoneNumber(seller.getContactNumber())
                    .receiverName(receiverInfo.getReceiverName())
                    .receiverPhoneNumber(receiverInfo.getReceiverPhoneNumber())
                    .receiverAddress(receiverInfo.getReceiverAddress())
                    .receiverPostcode(receiverInfo.getReceiverPostcode())
                    .seller(seller)
                    .user(order.getUser())
                    .orderItem(orderItem)
                    .build();

//            virtualDeliveryCompany.setOrderItem(orderItem);
            virtualDeliveryCompanyRepository.save(virtualDeliveryCompany);
        }
    }

    public TrackPackageResponse trackMyPackages(final String trackingNumber, final User user) {
        final VirtualDeliveryCompany virtualDeliveryCompany = virtualDeliveryCompanyRepository.findFirstByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new NotValidException("해당 운송장이 존재하지 않습니다"));

        if (Objects.equals(user.getId(), virtualDeliveryCompany.getUser().getId())) {
            VirtualDeliveryResponse virtualDeliveryResponse = VirtualDeliveryResponse.builder()
                    .virtualDeliveryCompanyId(virtualDeliveryCompany.getId())
                    .trackingNumber(virtualDeliveryCompany.getTrackingNumber())
                    .packageName(virtualDeliveryCompany.getPackageName())

                    .senderName(virtualDeliveryCompany.getSenderName())
                    .senderAddress(virtualDeliveryCompany.getSenderAddress())
                    .senderPostcode(virtualDeliveryCompany.getSenderPostcode())
                    .senderPhoneNumber(virtualDeliveryCompany.getSenderPhoneNumber())

                    .receiverName(virtualDeliveryCompany.getReceiverName())
                    .receiverPhoneNumber(virtualDeliveryCompany.getReceiverPhoneNumber())
                    .receiverAddress(virtualDeliveryCompany.getReceiverAddress())
                    .receiverPostcode(virtualDeliveryCompany.getReceiverPostcode())

                    .sellerId(virtualDeliveryCompany.getSeller().getId())
                    .userId(virtualDeliveryCompany.getUser().getId())
                    .build();

            return TrackPackageResponse.builder()
                    .virtualDeliveryResponse(virtualDeliveryResponse)
                    .build();
        }
        final VirtualDeliveryResponseForAnonymous virtualDeliveryResponseForAnonymous = getVirtualDeliveryResponseForAnonymous(trackingNumber, virtualDeliveryCompany);

        return TrackPackageResponse.builder()
                .virtualDeliveryResponseForAnonymous(virtualDeliveryResponseForAnonymous)
                .build();
    }

    private VirtualDeliveryResponseForAnonymous getVirtualDeliveryResponseForAnonymous(final String trackingNumber, final VirtualDeliveryCompany virtualDeliveryCompany) {
        return VirtualDeliveryResponseForAnonymous.builder()
                .trackingNumber(trackingNumber)
                .senderName(commonService.anonymousName(virtualDeliveryCompany.getSenderName()))
                .receiverName(commonService.anonymousName(virtualDeliveryCompany.getReceiverName()))
                .build();
    }


}
