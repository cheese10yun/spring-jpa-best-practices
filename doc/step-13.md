# step-13: Query Dsl이용한 페이징 API 만들기


[step-12: 페이징 API 만들기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-12.md) 에서 JPA와 `Pageable`를 이용해서 간단한 페이징 API를 만들었습니다. 이번 포스팅에서는 Query Dsl 동적 쿼리를 이용해서 검색 페이징 API를 만들어 보겠습니다.

## 기초 작업

Maven을 기준으로 설명드리겠습니다. 아래의 코드를 `pom.xml`에 추가하고 `mvn compile`을 진행합니다.
```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
</dependency>

<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
</dependency>

<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```


![](/images/querydsl-path.png)

complie이 성공적으로 완료되면 `target/generated-sources/java` 디렉토리에 `QXXX` 클래스 파일 생성되는 것을 확인할 수 있습니다.




## Controller
```java
@RestController
@RequestMapping("accounts")
public class AccountController {

    @GetMapping
    public Page<AccountDto.Res> getAccounts(
            @RequestParam(name = "type") final AccountSearchType type,
            @RequestParam(name = "value", required = false) final String value,
            final PageRequest pageRequest
    ) {
        return accountSearchService.search(type, value, pageRequest.of()).map(AccountDto.Res::new);
    }
}

public enum AccountSearchType {
    EMAIL,
    NAME,
    ALL
}
```
* type은 `AccountSearchType` enum으로 검색 페이징을 위한 type을 의미합니다. 본 예제에서는 이메일, 이름, 전체 페이징 기능을 제공합니다. 
* `value`는 type에 대한 value를 의미합니다. 이메일 검색시에는 value에 검색하고자하는 값을 지정합니다.
* `PageRequest`는 [step-12: 페이징 API 만들기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-12.md)에서 사용한 객체를 그대로 사용 하면 됩니다.


검색을 위한 type은 `String` 객체로 관리하는 것보다 `enum`으로 관리하는 것이 훨씬 효율적이라고 생각합니다. 만약 위에서 지정한 type 이외의 값을 요청할 경우 예외처리, `Service`영역에서 추가적인 처리 등 다양한 관점에서 `enum`이 훨씬 효율적입니다.

## Service
```java
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
```
`QuerydslRepositorySupport`를 이용하면 동적 쿼리를 쉽게 만들수 있습니다. 객체 기반으로 쿼리를 만드는 것이라서 타입 세이프의 강점을 그대로 가질 수 있습니다. `QuerydslRepositorySupport` 추상 클래스를 상속 받고 기본 생성자를 통해서 조회 대상 엔티티 클래스를 지정합니다.


`search(...)` 메서드는 컨트롤러에서 넘겨 받은 `type`, `value`, `pageable`를 기반으로 동적 쿼리를 만드는 작업을 진행합니다.

QueryDsl에서 생성한 `QAccount` 객체를 기반으로 동적 쿼리 작업을 진행합니다. `switch`문을 통해서 각 타입에 맞는 쿼리문을 작성하고 있습니다. 우리가 일반적으로 작성하는 쿼리와 크게 다르지 않아 해당 코드는 이해하기 어렵지 않습니다. 이것이 QueryDsl이 갖는 장점이라고 생각합니다. 

`NAME` 타입인 경우에는 `firstName`, `lastName`에 대한 like 검색을 진행합니다. `ALL`같은 경우에는 이전에 작성했던 전체 페이징과 동일합니다. 

## 요청
![](/images/search-paging.png)

`NAME` 타입으로 `yun`으로 요청을 합니다. `firstName` or `lastName`에 `yun`이 들어가 있는 계정을 검색 합니다.

## 응답
```json
{
  "content": [
    {
      "email": {
        "value": "test001@test.com"
      },
      "password": {
        "value": "$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6",
        "expirationDate": "+20120-01-20T00:00:00",
        "failedCount": 0,
        "ttl": 1209604,
        "expiration": false
      },
      "fistName": "yun",
      "lastName": "jun",
      "address": {
        "address1": "address1",
        "address2": "address2",
        "zip": "002"
      }
    },
    {
      "email": {
        "value": "test008@test.com"
      },
      "password": {
        "value": "$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6",
        "expirationDate": "+20120-01-20T00:07:00",
        "failedCount": 0,
        "ttl": 1209604,
        "expiration": false
      },
      "fistName": "yun",
      "lastName": "builder",
      "address": {
        "address1": "address1",
        "address2": "address2",
        "zip": "002"
      }
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "pageSize": 4,
    "pageNumber": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "size": 4,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```

## SQL

```sql
    select
        account0_.id as id1_0_,
        account0_.address1 as address2_0_,
        account0_.address2 as address3_0_,
        account0_.zip as zip4_0_,
        account0_.created_at as created_5_0_,
        account0_.email as email6_0_,
        account0_.first_name as first_na7_0_,
        account0_.last_name as last_nam8_0_,
        account0_.password_expiration_date as password9_0_,
        account0_.password_failed_count as passwor10_0_,
        account0_.password_ttl as passwor11_0_,
        account0_.password as passwor12_0_,
        account0_.update_at as update_13_0_ 
    from
        account account0_ 
    where
        lower(account0_.first_name) like ? 
        or lower(account0_.last_name) like ? 
    order by
        account0_.created_at asc limit ?
```

리스트 조회에 대한 쿼리는 반드시 해당 쿼리가 어떻게 출력되는지 반드시 확인해야 합니다. 해당 객체는 연관관계 설정이 되어 있지 않아 N + 1문제가 발생할 여지가 없지만, 실무에서는 많은 객체와의 관계를 맺기 때문에 반드시 쿼리가 어떻게 동작하는지 확인해야 합니다.



