package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.ItemRepository;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.OrderRepository;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.point.Point;
import com.jay.shoppingmall.domain.point.PointRepository;
import com.jay.shoppingmall.domain.point.point_history.PointHistory;
import com.jay.shoppingmall.domain.point.point_history.PointHistoryRepository;
import com.jay.shoppingmall.domain.point.point_history.model.PointPercentage;
import com.jay.shoppingmall.domain.point.point_history.model.PointStatus;
import com.jay.shoppingmall.domain.point.point_history.model.PointType;
import com.jay.shoppingmall.domain.review.Review;
import com.jay.shoppingmall.domain.review.ReviewRepository;
import com.jay.shoppingmall.domain.review.Star;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ReviewWriteRequest;
import com.jay.shoppingmall.dto.response.review.ReviewAvailableCheckResponse;
import com.jay.shoppingmall.dto.response.review.ReviewResponse;
import com.jay.shoppingmall.exception.exceptions.ItemNotFoundException;
import com.jay.shoppingmall.exception.exceptions.ReviewException;
import com.jay.shoppingmall.service.handler.FileHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointRepository pointRepository;
    private final FileHandler fileHandler;
    private final ImageRepository imageRepository;
    private final ItemRepository itemRepository;

    public ReviewAvailableCheckResponse reviewWriteAvailableCheck(final Long orderItemId, final User user) {
        if (reviewRepository.findByUserIdAndOrderItemId(user.getId(), orderItemId).isPresent()) {
            throw new ReviewException("리뷰를 이미 작성하셨습니다");
        }
        final OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ItemNotFoundException("주문이 존재하지 않습니다"));

        final String name = orderItem.getItem().getName();
        final Long priceAtPurchase = orderItem.getPriceAtPurchase();
        final Integer quantity = orderItem.getQuantity();
        final int pointOnlyText = (int) ((priceAtPurchase * quantity) * PointPercentage.ONLY_TEXT.getPercentage());
        final int pointWithPicture = (int) ((priceAtPurchase * quantity) * PointPercentage.TEXT_AND_PICTURE.getPercentage());

        return ReviewAvailableCheckResponse.builder()
                .orderItemId(orderItem.getId())
                .name(name)
                .pointOnlyText(pointOnlyText)
                .pointWithPicture(pointWithPicture)
                .build();

    }

    //배송 확인. 유저의 권한 확인. 리뷰 내용에 따른 포인트 다르게. 포인트 히스토리. 포인트 적용.
    public ReviewResponse reviewWrite(final ReviewWriteRequest request, final User user, final List<MultipartFile> files) {
        final OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId())
                .orElseThrow(() -> new ReviewException("리뷰 작성이 불가능합니다"));

        if (!orderItem.getOrderDelivery().getDeliveryStatus().equals(DeliveryStatus.DELIVERED)) {
            throw new ReviewException("배송이 완료되지 않았습니다");
        }

        //포인트 계산
        final Long priceAtPurchase = orderItem.getPriceAtPurchase();
        final Integer quantity = orderItem.getQuantity();
        double pointPercentage = PointPercentage.ONLY_TEXT.getPercentage();
        if (files == null) {
            pointPercentage = PointPercentage.TEXT_AND_PICTURE.getPercentage();
        }
        //double to int safety 확인 필요.
        final int calculatedPoint = (int) ((priceAtPurchase * quantity) * pointPercentage);
        System.out.println("calculatedPoint = " + calculatedPoint);

        Point point = pointRepository.findByUserId(user.getId());

        if (point == null) {
            point = Point.builder()
                    .pointNow(0)
                    .user(user)
                    .build();
            pointRepository.save(point);
        }
        point.plusPoint(calculatedPoint);

        PointHistory pointHistory = PointHistory.builder()
                .point(point)
                .pointNumber(calculatedPoint)
                .pointStatus(PointStatus.PAID)
                .pointType(PointType.REVIEW)
                .build();
        pointHistoryRepository.save(pointHistory);
        //포인트 계산 끝

        final Star star = Star.getByValue(request.getStar());

        Review review = Review.builder()
                .star(star)
                .text(request.getText())
                .user(user)
                .orderItem(orderItem)
                .build();
        reviewRepository.save(review);

        List<String> reviewImages = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                final Image image = fileHandler.parseFilesInfo(file, ImageRelation.REVIEW, review.getId());
                imageRepository.save(image);
                reviewImages.add(fileHandler.getStringImage(image));
            }
        }

        //구매 확정
        orderItem.getOrderDelivery().paymentDone();

        return ReviewResponse.builder()
                .reviewId(review.getId())
                .reviewImages(reviewImages)
                .star(review.getStar().value())
                .build();
    }

}
