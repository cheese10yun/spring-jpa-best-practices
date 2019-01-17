package com.cheese.springjpa.Account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountSearchService accountSearchService;

    public AccountController(AccountService accountService, AccountSearchService accountSearchService) {
        this.accountService = accountService;
        this.accountSearchService = accountSearchService;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public AccountDto.Res signUp(@RequestBody @Valid final AccountDto.SignUpReq dto) {
        return new AccountDto.Res(accountService.create(dto));
    }

    @GetMapping
    public Page<AccountDto.Res> getAccounts(
            @RequestParam(name = "type") final AccountSearchType type,
            @RequestParam(name = "value", required = false) final String value,
            final Pageable pageable
    ) {

        final Page<Account> search = accountSearchService.search(type, value, pageable);
        return search.map(AccountDto.Res::new);

    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDto.Res getUser(@PathVariable final long id) {
        return new AccountDto.Res(accountService.findById(id));
    }


//    @RequestMapping(method = RequestMethod.GET)
//    @ResponseStatus(value = HttpStatus.OK)
//    public AccountDto.Res getUserByEmail(@Valid Email email) {
//        return new AccountDto.Res(accountService.findByEmail(email));
//    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public AccountDto.Res updateMyAccount(@PathVariable final long id, @RequestBody final AccountDto.MyAccountReq dto) {
        return new AccountDto.Res(accountService.updateMyAccount(id, dto));
    }
}
