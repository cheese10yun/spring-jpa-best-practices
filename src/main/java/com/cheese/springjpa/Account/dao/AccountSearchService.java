package com.cheese.springjpa.Account.dao;


import com.cheese.springjpa.Account.domain.Account;
import com.cheese.springjpa.Account.domain.QAccount;
import com.cheese.springjpa.Account.dto.AccountSearchType;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class AccountSearchService extends QuerydslRepositorySupport {

    public AccountSearchService() {
        super(Account.class);
    }

    public Page<Account> search(final AccountSearchType type, final String value, final Pageable pageable) {
        final QAccount account = QAccount.account;
        final JPQLQuery<Account> query;

        switch (type) {
            case EMAIL:
                query = from(account)
                        .where(account.email.value.likeIgnoreCase(value + "%"));
                break;
            case NAME:
                query = from(account)
                        .where(account.firstName.likeIgnoreCase(value + "%")
                                .or(account.lastName.likeIgnoreCase(value + "%")));
                break;
            case ALL:
                query = from(account).fetchAll();
                break;
            default:
                throw new IllegalArgumentException();
        }
        final List<Account> accounts = getQuerydsl().applyPagination(pageable, query).fetch();
        return new PageImpl<>(accounts, pageable, query.fetchCount());
    }

}
