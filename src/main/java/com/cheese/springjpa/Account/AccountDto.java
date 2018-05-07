package com.cheese.springjpa.Account;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

public class AccountDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpReq {

        @Valid
        private com.cheese.springjpa.Account.model.Email email;
        @NotEmpty
        private String fistName;
        @NotEmpty
        private String lastName;
        @NotEmpty
        private String password;
        @NotEmpty
        private String address1;
        @NotEmpty
        private String address2;
        @NotEmpty
        private String zip;
        @Builder
        public SignUpReq(com.cheese.springjpa.Account.model.Email email, String fistName, String lastName, String password, String address1, String address2, String zip) {
            this.email = email;
            this.fistName = fistName;
            this.lastName = lastName;
            this.password = password;
            this.address1 = address1;
            this.address2 = address2;
            this.zip = zip;
        }

        public Account toEntity() {
            return Account.builder()
                    .email(this.email)
                    .fistName(this.fistName)
                    .lastName(this.lastName)
                    .password(this.password)
                    .address1(this.address1)
                    .address2(this.address2)
                    .zip(this.zip)
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyAccountReq {
        private String address1;
        private String address2;
        private String zip;

        @Builder
        public MyAccountReq(String address1, String address2, String zip) {
            this.address1 = address1;
            this.address2 = address2;
            this.zip = zip;
        }

    }

    @Getter
    public static class Res {
        private com.cheese.springjpa.Account.model.Email email;
        private String fistName;
        private String lastName;
        private String address1;
        private String address2;
        private String zip;

        public Res(Account account) {
            this.email = account.getEmail();
            this.fistName = account.getFistName();
            this.lastName = account.getLastName();
            this.address1 = account.getAddress1();
            this.address2 = account.getAddress2();
            this.zip = account.getZip();
        }
    }
}
