package com.cheese.springjpa.delivery;

import com.cheese.springjpa.Account.model.Address;
import com.cheese.springjpa.delivery.log.DeliveryLog;
import com.cheese.springjpa.delivery.log.DeliveryStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DeliveryRepositoryTest {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    public void orderCreate() {

        final Delivery delivery = buildOrder();

        final DeliveryLog deliveryLog1 = buildOrderLog(delivery, DeliveryStatus.CANCEL);
        final DeliveryLog deliveryLog2 = buildOrderLog(delivery, DeliveryStatus.PENDING);
        final DeliveryLog deliveryLog3 = buildOrderLog(delivery, DeliveryStatus.PENDING);

        delivery.getLogs().add(deliveryLog1);
        delivery.getLogs().add(deliveryLog2);
        delivery.getLogs().add(deliveryLog3);

        final Delivery delivery1 = deliveryRepository.save(delivery);


        System.out.println(delivery1);


    }

    private Delivery buildOrder() {
        return Delivery.builder()
                .address(buildAddress())
                .build();
    }

    private DeliveryLog buildOrderLog(final Delivery delivery, final DeliveryStatus status) {
        return DeliveryLog.builder()
                .delivery(delivery)
                .status(status)
                .build();
    }

    private Address buildAddress() {
        return Address.builder()
                .address1("address1")
                .address2("address2")
                .zip("zip..")
                .build();
    }
}