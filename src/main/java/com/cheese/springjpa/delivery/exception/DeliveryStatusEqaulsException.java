package com.cheese.springjpa.delivery.exception;

import com.cheese.springjpa.delivery.DeliveryStatus;

public class DeliveryStatusEqaulsException extends RuntimeException {


    private DeliveryStatus status;

    public DeliveryStatusEqaulsException(DeliveryStatus status) {
        super(status.name() + " It can not be changed to the same state.");
        this.status = status;
    }
}
