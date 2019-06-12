package com.cheese.springjpa.Account.api;

import com.cheese.springjpa.Account.application.AccountService;
import com.cheese.springjpa.Account.dao.AccountRepository;
import com.cheese.springjpa.Account.dao.AccountSearchService;
import com.cheese.springjpa.Account.dto.AccountDto;
import com.cheese.springjpa.Account.dto.AccountDto.Res;
import com.cheese.springjpa.Account.dto.AccountSearchType;
import com.cheese.springjpa.common.model.PageRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountSearchService accountSearchService;
  private final AccountRepository accountRepository;

//    private final AccountRepository accountRepository;


    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Res signUp(@RequestBody @Valid final AccountDto.SignUpReq dto) {
        return new AccountDto.Res(accountService.create(dto));
    }

    @GetMapping
    public Page<AccountDto.Res> getAccounts(
            @RequestParam(name = "type") final AccountSearchType type,
            @RequestParam(name = "value", required = false) final String value,
            final PageRequest pageRequest
    ) {
        return accountSearchService.search(type, value, pageRequest.of()).map(AccountDto.Res::new);
    }

//    step-12 컨트롤러 코드
//    @GetMapping
//    public Page<AccountDto.Res> getAccounts(final PageRequest pageable) {
//        return accountService.findAll(pageable.of()).map(AccountDto.Res::new);
//    }

//    기본 Pageable을 사용한 코드
//    @GetMapping
//    public Page<AccountDto.Res> getAccounts(final Pageable pageable) {
//        return accountService.findAll(pageable).map(AccountDto.Res::new);
//    }

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
