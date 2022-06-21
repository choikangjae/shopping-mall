package com.jay.shoppingmall.service;

import com.jay.shoppingmall.domain.entitybuilder.EntityBuilder;
import com.jay.shoppingmall.domain.image.Image;
import com.jay.shoppingmall.domain.image.ImageRelation;
import com.jay.shoppingmall.domain.image.ImageRepository;
import com.jay.shoppingmall.domain.item.Item;
import com.jay.shoppingmall.domain.item.item_option.ItemOption;
import com.jay.shoppingmall.domain.model.page.PageDto;
import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.domain.order.Order;
import com.jay.shoppingmall.domain.order.order_item.OrderItem;
import com.jay.shoppingmall.domain.order.order_item.OrderItemRepository;
import com.jay.shoppingmall.domain.order.order_item.order_delivery.OrderDelivery;
import com.jay.shoppingmall.domain.payment.Payment;
import com.jay.shoppingmall.domain.payment.model.ReceiverInfo;
import com.jay.shoppingmall.domain.point.Point;
import com.jay.shoppingmall.domain.point.PointRepository;
import com.jay.shoppingmall.domain.point.point_history.PointHistoryRepository;
import com.jay.shoppingmall.domain.review.Review;
import com.jay.shoppingmall.domain.review.ReviewRepository;
import com.jay.shoppingmall.domain.review.Star;
import com.jay.shoppingmall.domain.seller.Seller;
import com.jay.shoppingmall.domain.user.User;
import com.jay.shoppingmall.dto.request.ReviewWriteRequest;
import com.jay.shoppingmall.dto.response.review.OrderItemForReviewResponse;
import com.jay.shoppingmall.dto.response.review.ReviewResponse;
import com.jay.shoppingmall.exception.exceptions.ReviewException;
import com.jay.shoppingmall.service.common.CommonService;
import com.jay.shoppingmall.service.handler.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    ReviewService reviewService;

    @Mock
    ReviewRepository reviewRepository;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    PointHistoryRepository pointHistoryRepository;
    @Mock
    PointRepository pointRepository;
    @Mock
    ImageRepository imageRepository;

    @Mock
    PaymentService paymentService;
    @Mock
    FileHandler fileHandler;

    @Spy
    CommonService commonService;

    @Mock
    MultipartFile multipartFile;

    User user;
    Seller seller;
    Item item;
    ItemOption itemOption;
    private OrderItem orderItem;
    private OrderItem orderItemNotDelivered;
    private OrderDelivery delivered;
    private OrderDelivery notDelivered;
    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        user = EntityBuilder.getUser();
        seller = EntityBuilder.getSeller();
        item = EntityBuilder.getItem();
        itemOption = EntityBuilder.getItemOption();

        final ReceiverInfo receiverInfo = ReceiverInfo.builder()
                .receiverName("수신자 이름")
                .receiverAddress("서울시 종로")
                .receiverEmail("qwerty@email.com")
                .receiverPhoneNumber("010-1234-1234")
                .receiverPostcode("12345")
                .build();

        payment = Payment.builder()
                .user(user)
                .receiverInfo(receiverInfo)
                .build();

        order = Order.builder()
                .user(user)
                .payment(payment)
                .build();

        delivered = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .build();

        notDelivered = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.DELIVERING)
                .build();

        orderItem = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .seller(seller)
                .order(order)
                .mainImageId(0L)
                .orderDelivery(delivered)
                .build();

        orderItemNotDelivered = OrderItem.builder()
                .id(0L)
                .priceAtPurchase(30000L)
                .quantity(5)
                .item(item)
                .itemOption(itemOption)
                .seller(seller)
                .order(order)
                .mainImageId(0L)
                .orderDelivery(notDelivered)
                .build();
    }

    @Test
    void whenSessionUserIsNotEqualToOrderItemUser_ThrowException_orderItemRequestForReview() {
        User sessionUser = EntityBuilder.getUser2();

        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(orderItem));

        final ReviewException reviewException = assertThrows(ReviewException.class, () -> reviewService.orderItemRequestForReview(0L, sessionUser));
        assertThat(reviewException.getMessage()).contains("리뷰 작성이 불가능합니다");
    }

    @Test
    void whenDeliveryIsNotDone_ThrowException_orderItemRequestForReview() {
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(orderItemNotDelivered));

        final ReviewException reviewException = assertThrows(ReviewException.class, () -> reviewService.orderItemRequestForReview(0L, user));
        assertThat(reviewException.getMessage()).contains("배송이 완료되지 않았습니다");
    }

    @Test
    void whenSessionUserIsEqualToOrderItemUser_AndIsDelivered_orderItemRequestForReview() {
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(orderItem));

        final OrderItemForReviewResponse orderItemForReviewResponse = reviewService.orderItemRequestForReview(0L, user);

        assertThat(orderItemForReviewResponse).isNotNull();
        assertThat(orderItemForReviewResponse.getPointOnlyText()).isEqualTo(750);
        assertThat(orderItemForReviewResponse.getPointWithPicture()).isEqualTo(orderItemForReviewResponse.getPointOnlyText() * 2);
    }

    @Test
    @DisplayName("리뷰 이미지가 첨부되지 않으면 0.5퍼센트만 적립된다")
    void whenNoReviewImage_PointZeroDotFivePercent_reviewWrite() {
        ReviewWriteRequest reviewWriteRequest = ReviewWriteRequest.builder()
                .orderItemId(0L)
                .star(5)
                .text("리뷰")
                .build();

        Point point = Point.builder()
                .pointNow(0)
                .user(user)
                .build();

        doNothing().when(paymentService).moneyTransactionToSeller(any());
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(orderItem));
        when(pointRepository.findByUserId(any())).thenReturn(point);

        final ReviewResponse reviewResponse = reviewService.reviewWrite(reviewWriteRequest, user, null);

        verify(reviewRepository).save(any());
        assertThat(reviewResponse.getStar()).isEqualTo(5);
        assertThat(reviewResponse.getReviewImages()).isEmpty();

        verify(pointHistoryRepository).save(any());
        assertThat(point.getPointNow()).isEqualTo(750);
    }
    @Test
    void whenNoPointEntityExists_PointEntityWillBeCreated_reviewWrite() {
        ReviewWriteRequest reviewWriteRequest = ReviewWriteRequest.builder()
                .orderItemId(0L)
                .star(5)
                .text("리뷰")
                .build();

        doNothing().when(paymentService).moneyTransactionToSeller(any());
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(orderItem));

        final ReviewResponse reviewResponse = reviewService.reviewWrite(reviewWriteRequest, user, null);

        verify(pointRepository).save(any());
    }
    @Test
    void whenWithReviewImage_PointOnePercent_reviewWrite() {
        List<MultipartFile> fileList = new ArrayList<>();
        fileList.add(multipartFile);
        fileList.add(multipartFile);
        fileList.add(multipartFile);

        ReviewWriteRequest reviewWriteRequest = ReviewWriteRequest.builder()
                .orderItemId(0L)
                .star(5)
                .text("리뷰")
                .build();

        Point point = Point.builder()
                .pointNow(0)
                .user(user)
                .build();

        List<String> strings = new ArrayList<>();
        strings.add("BASE64 Encoded image");
        strings.add("BASE64 Encoded image");

        doNothing().when(paymentService).moneyTransactionToSeller(any());
        when(orderItemRepository.findById(any())).thenReturn(Optional.ofNullable(orderItem));
        when(pointRepository.findByUserId(any())).thenReturn(point);
        when(fileHandler.getStringImages(any(), any(), any())).thenReturn(strings);

        final ReviewResponse reviewResponse = reviewService.reviewWrite(reviewWriteRequest, user, fileList);

        verify(reviewRepository).save(any());
        assertThat(reviewResponse.getStar()).isEqualTo(5);
        assertThat(reviewResponse.getReviewImages()).isNotEmpty();

        verify(pointHistoryRepository).save(any());
        assertThat(point.getPointNow()).isEqualTo(1500);
    }

    @Test
    void getItemReviews() {
        List<Review> reviews = new ArrayList<>();
        Review review = Review.builder()
                .star(Star.getByValue(5))
                .orderItem(orderItem)
                .item(item)
                .user(user)
                .build();
        reviews.add(review);
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Review> reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

        Image image = mock(Image.class);
        List<Image> imageList = new ArrayList<>();
        imageList.add(image);
        imageList.add(image);

        when(reviewRepository.findAllByItemIdOrderByCreatedDateDesc(any(), any())).thenReturn(reviewPage);
        when(imageRepository.findAllByImageRelationAndForeignId(any(), any())).thenReturn(imageList);
        when(fileHandler.getStringImage(any())).thenReturn("BASE64 Encoded Image");

        final PageDto itemReviews = reviewService.getItemReviews(0L, Pageable.unpaged());

        ReviewResponse reviewResponse = (ReviewResponse) itemReviews.getContent().get(0);
        assertThat(itemReviews.getContent()).isNotEmpty();
        assertThat(reviewResponse.getReviewImages().size()).isEqualTo(2);
    }

    @Test
    void getMyRecentReviews() {
        List<Review> reviews = new ArrayList<>();
        Review review = Review.builder()
                .star(Star.getByValue(5))
                .orderItem(orderItem)
                .item(item)
                .user(user)
                .build();
        reviews.add(review);

        when(reviewRepository.findFirst10ByUserIdOrderByCreatedDateDesc(any())).thenReturn(reviews);
        when(fileHandler.getStringImage(any())).thenReturn("BASE64 Encoded Image");

        final List<ReviewResponse> reviewResponses = reviewService.getMyRecentReviews(user);

        assertThat(reviewResponses.get(0).getReviewImages()).isNull();
        assertThat(reviewResponses.get(0).getItemImage()).isEqualTo("BASE64 Encoded Image");
    }
}