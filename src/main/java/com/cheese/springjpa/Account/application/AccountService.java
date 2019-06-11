package com.cheese.springjpa.Account.application;

import com.cheese.springjpa.Account.dao.AccountRepository;
import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.Email;
import com.cheese.springjpa.Account.dto.AccountDto;
import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.Account.exception.EmailDuplicationException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;


    @Transactional(readOnly = true)
    public Account findById(long id) {
        final Optional<Account> account = accountRepository.findById(id);
        account.orElseThrow(() -> new AccountNotFoundException(id));
        return account.get();
    }

    @Transactional(readOnly = true)
    public Account findByEmail(final Email email) {
        final Account account = accountRepository.findByEmail(email);
        if (account == null) throw new AccountNotFoundException(email);
        return account;
    }

//    @Transactional(readOnly = true)
//    public Page<Account> findAll(Pageable pageable) {
//        return accountRepository.findAll(pageable);
//    }

    @Transactional(readOnly = true)
    public boolean isExistedEmail(Email email) {
        return accountRepository.findByEmail(email) != null;
    }

    public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
        final Account account = findById(id);
        account.updateMyAccount(dto);
        return account;
    }

    public Account create(AccountDto.SignUpReq dto) {
        if (isExistedEmail(dto.getEmail()))
            throw new EmailDuplicationException(dto.getEmail());
        return accountRepository.save(dto.toEntity());
    }

}
