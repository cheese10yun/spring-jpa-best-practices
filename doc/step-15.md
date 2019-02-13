# step-15: Querydsl를 이용해서 Repository 확장하기


## Repository Code
```java
public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustomRepository {

    Account findByEmail(Email email);

    boolean existsByEmail(Email email);

    List<Account> findDistinctFirstBy...

    @Query("select *from....")
    List<Account> findXXX();
}
```

JpaRepository를 이용해서 복잡한 쿼리는 작성하기가 어려운점이 있습니다. `findByEmail`, `existsByEmail` 같은 유니크한 값을 조회하는 것들은 쿼리 메서드로 표현하는 것이 가독성 및 생산성에 좋습니다.

**하지만 복잡한 쿼리가 복잡해지면 쿼리 메서드로 표현하기도 어렵습니다. `@Query` 어노테이션을 이용해서 JPQL을 작성하는 것도 방법이지만 type safe 하지 않아 유지 보수하기 어려운 단점이 있습니다.**

이러한 단점은 `Querydsl`를 통해서 해결할 수 있지만 조회용 DAO 클래스 들이 남발되어 다양한 DAO를 DI 받아 비즈니스 로직을 구현하게 되는 현상이 발생하게 됩니다.

이러한 문제를 상속 관계를 통해 `XXXRepository` 객체를 통해서 DAO를 접근할 수 있는 패턴을 포스팅 하려 합니다.

![](/images/AccountRepository.png)

클래스 다이어그램을 보면 `AccountRepository`는 `AccountCustomRepository`, `JpaRepository`를 구현하고 있습니다.

`AccountRepository`는 `JpaRepository`를 구현하고 있으므로 `findById`, `save` 등의 메서드를 정의하지 않고도 사용 가능했듯이 `AccountCustomRepository`에 있는 메서드도 `AccountRepository`에서 그대로 사용 가능합니다.

즉 우리는 `AccountCustomRepositoryImpl`에게 복잡한 쿼리는 구현을 시키고 `AccountRepository` 통해서 마치 `JpaRepository`를 사용하는 것처럼 편리하게 사용할 수 있습니다.


## Code

```java
public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustomRepository {
    Account findByEmail(Email email);
    boolean existsByEmail(Email email);
}

public interface AccountCustomRepository {
    List<Account> findRecentlyRegistered(int limit);
}

@Transactional(readOnly = true)
public class AccountCustomRepositoryImpl extends QuerydslRepositorySupport implements AccountCustomRepository {

    public AccountCustomRepositoryImpl() {
        super(Account.class);
    }

    @Override
    // 최근 가입한 limit 갯수 만큼 유저 리스트를 가져온다
    public List<Account> findRecentlyRegistered(int limit) {
        final QAccount account = QAccount.account;
        return from(account)
                .limit(limit)
                .orderBy(account.createdAt.desc())
                .fetch();
    }
}
```
* `AccountCustomRepository` 인터페이스를 생성합니다.
* `AccountRepository` 인터페이스에 방금 생성한 `AccountCustomRepository` 인터페이스를 `extends` 합니다.
* `AccountCustomRepositoryImpl`는 실제 Querydsl를 이용해서 `AccountCustomRepository`의 세부 구현을 진행합니다. 

**커스텀 Repository를 만들 때 중요한 것은 `Impl` 네이밍을 지켜야합니다.** 자세한 것은
[Spring Data JPA - Reference Documentation](https://docs.spring.io/spring-data/jpa/docs/2.1.3.RELEASE/reference/html/#repositories.custom-implementations)을 참조해주세요

## Test Code

```java
@DataJpaTest
@RunWith(SpringRunner.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    public void findByEmail_test() {
        final String email = "test001@test.com";
        final Account account = accountRepository.findByEmail(Email.of(email));
        assertThat(account.getEmail().getValue()).isEqualTo(email);
    }

    @Test
    public void isExistedEmail_test() {
        final String email = "test001@test.com";
        final boolean existsByEmail = accountRepository.existsByEmail(Email.of(email));
        assertThat(existsByEmail).isTrue();
    }

    @Test
    public void findRecentlyRegistered_test() {
        final List<Account> accounts = accountRepository.findRecentlyRegistered(10);
        assertThat(accounts.size()).isLessThan(11);
    }
}
```
`findByEmail_test`, `isExistedEmail_test` 테스트는 `AccountRepository`에 작성된 쿼리메서드 테스트입니다. 

중요한 부분은 `findRecentlyRegistered_test` 으로 `AccountCustomRepository`에서 정의된 메서드이지만 `accountRepository`를 이용해서 호출하고 있습니다. 

즉 `accountRepository` 객체를 통해서 
 복잡한 쿼리의 세부 구현체 객체를 구체적으로 알 필요 없이 사용할 수 있습니다. **이는 의존성을 줄일 수 있는 좋은 구조라고 생각합니다.**

## 결론
`Repository`에서 복잡한 조회 쿼리를 작성하는 것은 유지 보스 측면에서 좋지 않습니다. 쿼리 메서드로 표현이 어려우며 `@Qeury` 어노테이션을 통해서 작성된 쿼리는 type safe하지 않은 단점이 있습니다. 이것을 **QueryDsl으로 해결하고 다형성을 통해서 복잡한 쿼리의 세부 구현은 감추고 `Repository`를 통해서 사용하도록 하는 것이 핵심입니다.**