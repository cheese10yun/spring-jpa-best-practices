package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account create(AccountDto.SignUpReq dto) {
        return accountRepository.save(dto.toEntity());
    }

    @Transactional(readOnly = true)
    public Account findById(long id) {
        final Account account = accountRepository.findOne(id);
        if (account == null)
            throw new AccountNotFoundException(id);
        return account;
    }

    public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
        final Account account = findById(id);
        account.updateMyAccount(dto);
        return account;
    }
}
