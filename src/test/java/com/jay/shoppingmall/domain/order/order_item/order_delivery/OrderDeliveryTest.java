package com.jay.shoppingmall.domain.order.order_item.order_delivery;

import com.jay.shoppingmall.domain.order.DeliveryStatus;
import com.jay.shoppingmall.exception.exceptions.DeliveryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderDeliveryTest {

    OrderDelivery paymentDone;
    OrderDelivery delivering;
    OrderDelivery delivered;
    OrderDelivery finished;

    @BeforeEach
    void setUp() {
        paymentDone = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.PAYMENT_DONE)
                .build();
        delivering = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.DELIVERING)
                .build();
        delivered = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .build();
        finished = OrderDelivery.builder()
                .deliveryStatus(DeliveryStatus.FINISHED)
                .build();
    }

    @Test
    void whenStatusIsFinished_ThrowDeliveryException_deliveryStart() {
        assertThrows(DeliveryException.class, () -> delivering.deliveryStart());

    }

    @Test
    void whenStatusIsFinished_ThrowDeliveryException_deliveryDone() {
        assertThrows(DeliveryException.class, () -> delivered.deliveryDone());
    }

    @Test
    void whenStatusIsFinished_ThrowDeliveryException_paymentDone() {
        assertThrows(DeliveryException.class, () -> finished.paymentDone());
    }
}