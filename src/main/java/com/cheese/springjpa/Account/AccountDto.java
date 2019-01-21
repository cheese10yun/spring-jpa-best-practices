package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.model.Address;
import com.cheese.springjpa.Account.model.Email;
import com.cheese.springjpa.Account.model.Password;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

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

        private String password;

        @Valid
        private Address address;

        @Builder
        public SignUpReq(Email email, String fistName, String lastName, String password, Address address) {
            this.email = email;
            this.fistName = fistName;
            this.lastName = lastName;
            this.password = password;
            this.address = address;
        }

        public Account toEntity() {
            return Account.builder()
                    .email(this.email)
                    .firstName(this.fistName)
                    .lastName(this.lastName)
                    .password(Password.builder().value(this.password).build())
                    .address(this.address)
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyAccountReq {
        private Address address;

        @Builder
        public MyAccountReq(final Address address) {
            this.address = address;
        }

    }

    @Getter
    public static class Res {
        private com.cheese.springjpa.Account.model.Email email;
        private Password password;
        private String fistName;
        private String lastName;
        private Address address;

        public Res(Account account) {
            this.email = account.getEmail();
            this.fistName = account.getFirstName();
            this.lastName = account.getLastName();
            this.address = account.getAddress();
            this.password = account.getPassword();
        }
    }
}
