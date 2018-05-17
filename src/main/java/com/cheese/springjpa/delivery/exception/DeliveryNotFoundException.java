package com.cheese.springjpa.delivery.exception;


import lombok.Getter;

@Getter
public class DeliveryNotFoundException extends RuntimeException {

    private long id;

    public DeliveryNotFoundException(long id) {
        super(id + " is not found");
        this.id = id;
    }

}
