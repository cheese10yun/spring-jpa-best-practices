package com.cheese.springjpa.delivery;

import com.cheese.springjpa.account.model.Address;
import com.cheese.springjpa.common.model.DateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryDto {


    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    public static class CreationReq {
        @Valid
        private Address address;

        @Builder
        public CreationReq(Address address) {
            this.address = address;
        }

        public Delivery toEntity() {
            return Delivery.builder()
                    .address(address)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateReq {
        private DeliveryStatus status;

        @Builder
        public UpdateReq(DeliveryStatus status) {
            this.status = status;
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Res {
        private Address address;
        private List<LogRes> logs;

        public Res(final Delivery delivery) {
            this.address = delivery.getAddress();
            this.logs = delivery.getLogs()
                    .parallelStream().map(LogRes::new)
                    .collect(Collectors.toList());
        }
    }


    @Getter
    public static class LogRes {
        private DeliveryStatus status;
        private DateTime dateTime;

        public LogRes(DeliveryLog log) {
            this.status = log.getStatus();
            this.dateTime = log.getDateTime();
        }
    }
}
