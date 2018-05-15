package com.cheese.springjpa.delivery;

import com.cheese.springjpa.delivery.exception.DeliveryAlreadyDeliveringException;
import com.cheese.springjpa.delivery.exception.DeliveryStatusEqaulsException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeliveryLogTest {

    @Test
    public void delivery_pending_로그저장() {
        final DeliveryStatus status = DeliveryStatus.PENDING;
        final DeliveryLog log = buildLog(buildDelivery(), status);

        assertThat(status, is(log.getStatus()));
    }

    @Test
    public void delivery_delivering() {
        final Delivery delivery = buildDelivery();
        final DeliveryStatus status = DeliveryStatus.PENDING;

        delivery.getLogs().add(buildLog(delivery, status));
        delivery.getLogs().add(buildLog(delivery, DeliveryStatus.DELIVERING));
    }

    @Test
    public void delivery_canceled() {
        final Delivery delivery = buildDelivery();
        final DeliveryStatus status = DeliveryStatus.PENDING;

        delivery.getLogs().add(buildLog(delivery, status));
        delivery.getLogs().add(buildLog(delivery, DeliveryStatus.CANCELED));
    }


    @Test
    public void delivery_completed() {
        final Delivery delivery = buildDelivery();
        final DeliveryStatus status = DeliveryStatus.DELIVERING;

        delivery.getLogs().add(buildLog(delivery, status));
        delivery.getLogs().add(buildLog(delivery, DeliveryStatus.COMPLETED));
    }

    @Test(expected = DeliveryStatusEqaulsException.class)
    public void 동일한_status_변경시_DeliveryStatusEqaulsException() {
        final Delivery delivery = buildDelivery();
        final DeliveryStatus status = DeliveryStatus.DELIVERING;

        delivery.getLogs().add(buildLog(delivery, status));
        delivery.getLogs().add(buildLog(delivery, DeliveryStatus.DELIVERING));
    }

    @Test(expected = DeliveryAlreadyDeliveringException.class)
    public void 배송시작후_취소시_DeliveryAlreadyDeliveringException() {
        final Delivery delivery = buildDelivery();
        final DeliveryStatus status = DeliveryStatus.DELIVERING;

        delivery.getLogs().add(buildLog(delivery, status));
        delivery.getLogs().add(buildLog(delivery, DeliveryStatus.CANCELED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void 완료상태_변경시_IllegalArgumentException() {
        final Delivery delivery = buildDelivery();
        final DeliveryStatus status = DeliveryStatus.COMPLETED;

        delivery.getLogs().add(buildLog(delivery, status));
        delivery.getLogs().add(buildLog(delivery, DeliveryStatus.CANCELED));

    }


    private DeliveryLog buildLog(Delivery delivery, DeliveryStatus status) {
        return DeliveryLog.builder()
                .delivery(delivery)
                .status(status)
                .build();
    }

    private Delivery buildDelivery() {
        return Delivery.builder()
                    .build();
    }


}