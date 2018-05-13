package com.cheese.springjpa.delivery;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("orders")
@AllArgsConstructor
public class DeliveryController {


    private DeliveryService deliveryService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Delivery signUp(@RequestBody @Valid final Delivery delivery) {
        return deliveryService.create(delivery);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Delivery getUser(@PathVariable final long id) {
        return deliveryService.findById(id);
    }

}
