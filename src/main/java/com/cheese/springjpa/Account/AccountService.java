package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.Account.exception.EmailDuplicationException;
import com.cheese.springjpa.Account.model.Email;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account create(AccountDto.SignUpReq dto) {

        if (isExistedEmail(dto.getEmail()))
            throw new EmailDuplicationException(dto.getEmail());

        return accountRepository.save(dto.toEntity());
    }

    public Account findById(long id) {
        final Account account = accountRepository.findOne(id);
        if (account == null) throw new AccountNotFoundException(id);
        return account;
    }

    public Account findByEmail(final Email email) {
        final Account account = accountRepository.findByEmail(email);
        if (account == null) throw new AccountNotFoundException(email);
        return account;
    }

    public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
        final Account account = findById(id);
        account.updateMyAccount(dto);
        return account;
    }

    @Transactional(readOnly = true)
    public boolean isExistedEmail(com.cheese.springjpa.Account.model.Email email) {
        return accountRepository.findByEmail(email) != null;
    }
}
