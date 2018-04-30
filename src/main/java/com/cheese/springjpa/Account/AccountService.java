package com.cheese.springjpa.Account;

import com.cheese.springjpa.Account.exception.AccountNotFoundException;
import com.cheese.springjpa.error.ErrorCode;
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

    @Transactional(readOnly = true)
    public boolean isExistedEmail(String email) {
        return accountRepository.findByEmail(email) != null;
    }
}
