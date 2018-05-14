package com.cheese.springjpa.delivery;

import com.cheese.springjpa.delivery.exception.DeliveryNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class DeliveryService {

    private DeliveryRepository deliveryRepository;

    public Delivery create(DeliveryDto.CreationReq dto) {
        final Delivery delivery = dto.toEntity();
        final DeliveryLog log = buildOrderLog(delivery, DeliveryStatus.PENDING);
        delivery.getLogs().add(log);
        return deliveryRepository.save(delivery);
    }

    public Delivery updateStatus(long id, DeliveryDto.UpdateReq dto) {
        final Delivery delivery = findById(id);
        final DeliveryLog log = buildOrderLog(delivery, dto.getStatus());
        delivery.getLogs().add(log);
        return delivery;
    }


    public Delivery findById(long id) {
        final Delivery delivery = deliveryRepository.findOne(id);
        if (delivery == null) throw new DeliveryNotFoundException(id);
        return delivery;
    }


    private DeliveryLog buildOrderLog(final Delivery delivery, final DeliveryStatus status) {
        return DeliveryLog.builder()
                .delivery(delivery)
                .status(status)
                .build();
    }

}
