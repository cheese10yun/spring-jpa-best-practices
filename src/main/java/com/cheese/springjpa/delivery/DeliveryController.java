package com.cheese.springjpa.delivery;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("deliveries")
@AllArgsConstructor
public class DeliveryController {


    private DeliveryService deliveryService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public DeliveryDto.Res create(@RequestBody @Valid final DeliveryDto.CreationReq dto) {
        return new DeliveryDto.Res(deliveryService.create(dto));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public DeliveryDto.Res getDelivery(@PathVariable final long id) {
        return new DeliveryDto.Res(deliveryService.findById(id));
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public DeliveryDto.Res updateDelivery(@PathVariable final long id, @RequestBody DeliveryDto.UpdateReq dto) {
        return new DeliveryDto.Res(deliveryService.updateStatus(id, dto));
    }

}
