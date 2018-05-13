package com.cheese.springjpa.delivery;

import com.cheese.springjpa.delivery.log.DeliveryLog;
import com.cheese.springjpa.delivery.log.DeliveryStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class DeliveryService {

    private DeliveryRepository deliveryRepository;

    public Delivery create(Delivery delivery) {


        final DeliveryLog deliveryLog1 = buildOrderLog(delivery, DeliveryStatus.CANCEL);
        final DeliveryLog deliveryLog2 = buildOrderLog(delivery, DeliveryStatus.CANCEL);

        delivery.getLogs().add(deliveryLog1);
        delivery.getLogs().add(deliveryLog2);

        return deliveryRepository.save(delivery);
    }



    public Delivery findById(long id) {
        return deliveryRepository.findOne(id);
    }


    private DeliveryLog buildOrderLog(final Delivery delivery, final DeliveryStatus status) {
        return DeliveryLog.builder()
                .delivery(delivery)
                .status(status)
                .build();
    }
}
