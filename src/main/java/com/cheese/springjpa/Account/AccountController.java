package com.cheese.springjpa.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public AccountDto.Res signUp(@RequestBody AccountDto.SignUpReq dto) {
        return new AccountDto.Res(accountService.create(dto));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDto.Res getUser(@PathVariable long id) {
        return new AccountDto.Res(accountService.findById(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDto.Res updateMyAccount(@PathVariable long id, @RequestBody AccountDto.MyAccountReq dto) {
        return new AccountDto.Res(accountService.updateMyAccount(id, dto));
    }


}
