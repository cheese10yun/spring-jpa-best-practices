# step-12: Paging API 만들기

JPA를 이용해서 Paging API를 만들어 보도록 하겠습니다. 페이징 처리는 거의 모든 웹 개발에서 사용하고 있습니다. 그렇게 복잡하고 어려운 구현은 아니나 실제 쿼리로 작성할 때는 상당히 번거로운 작업이 됩니다. 또 데이터베이스마다 페이징 쿼리가 조금씩 다르다는 점도 복잡도를 높이는 요인 중 하나입니다. 

Spring Data JPA에서는 이러한 문제를 아주 쉽게 해결할 수 있어 핵심 비즈니스 로직에 집중할 수 있게 해줍니다. 지금부터 예제를 설명하겠습니다.

## 기초 작업
![](/images/jpa-class-diagram.png)

이전에 작성한 `AccountRepository`는 `JpaRepository`를 상속하고 있습니다. `JpaRepository`는 `PagingAndSortingRepository` 클래스를 상속 받고 있습니다. 페이징 작업을 위한 작업은 `AccountRepository`를 작성한 순간 대부분 끝난 것입니다. 

`CurdRepository`를 상속하는 것보다 하위 클래스인 `JpaRepository`를 상속 받아 `Repository`를 구현하는 것이 좋습니다.

![](/images/data-jpa-paging.png)

페이징 처리하는 메서드도 간단합니다. 매게변수로 `Pageable` 받아 `Page<T>`으로 리턴해줍니다.

## Sample Code
```java

@RestController
@RequestMapping("accounts")
public class AccountController {
    @GetMapping
    public Page<AccountDto.Res> getAccounts(final Pageable pageable) {
        return accountService.findAll(pageable).map(AccountDto.Res::new);
    }
}

@Service
@Transactional
@AllArgsConstructor
public class AccountService {
    ...
    @Transactional(readOnly = true)
    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }
}
```
정말 간단합니다. 컨롤어에서 `Pageable` 인터페이스를 받고 `repository` 메서드 `findAll(pageable)`로 넘기기만 하면됩니다.


### 요청
```bash
curl -X GET \
  http://localhost:8080/accounts
```

### 응답
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
      "fistName": "first",
      "lastName": "last",
      "address": {
        "address1": "address1",
        "address2": "address2",
        "zip": "002"
      }
    }
    ...
  ],
  "pageable": {
    "sort": {
      "sorted": false,
      "unsorted": true,
      "empty": true
    },
    "offset": 0,
    "pageSize": 20,
    "pageNumber": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true, // 마지막 페이지 여부
  "totalPages": 1, // 전체 페이지가 1개
  "totalElements": 13, // 모든 요소는 13 개
  "size": 20, // 한 페이지에서 보여줄 사이즈의 갯수, size를 제한하지 않으면 기본적으로 20으로 초기화 된다.
  "number": 0,
  "sort": {
    "sorted": false,
    "unsorted": true,
    "empty": true
  },
  "numberOfElements": 13,
  "first": true, // 첫 패이지 여부
  "empty": false // 리스트가 비어 있는지 여부
}
```
Response에 상세한 정보들을 내려주고 있습니다. 페이징 하단의 네비게이션을 작성할 때 유용할 정보들이 있습니다. 이렇게 Spring Data JPA를 이용하면 페이징 기능을 간편하게 만들 수 있습니다.

### 다양한 요청
![](/images/postman-page-request.png)

```json
{
    "content": [
        {
            "email": {
                "value": "test013@test.com"
            },
            "password": {
                "value": "$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6",
                "expirationDate": "+20120-01-20T00:12:00",
                "failedCount": 0,
                "ttl": 1209604,
                "expiration": false
            },
            "fistName": "first",
            "lastName": "last",
            "address": {
                "address1": "address1",
                "address2": "address2",
                "zip": "002"
            }
        },
        {
            "email": {
                "value": "test012@test.com"
            },
            "password": {
                "value": "$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6",
                "expirationDate": "+20120-01-20T00:11:00",
                "failedCount": 0,
                "ttl": 1209604,
                "expiration": false
            },
            "fistName": "first",
            "lastName": "last",
            "address": {
                "address1": "address1",
                "address2": "address2",
                "zip": "002"
            }
        }
    ],
    "pageable": {
        "sort": {
            "unsorted": false,
            "sorted": true,
            "empty": false
        },
        "offset": 0,
        "pageSize": 2,
        "pageNumber": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 7,
    "totalElements": 13,
    "size": 2,
    "number": 0,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "numberOfElements": 2,
    "first": true,
    "empty": false
}
```


`Pageable`은 다양한 요청 이용해서 기본적인 정렬 기능을 제공합니다. `page`는 실제 페이지를 의미하고 `size`는 `content`의 size를 의미합니다. `sort`는 페이징을 처리 시 정렬을 값을 의미합니다. `id,DESC`는 id 기준으로 내림차순 정렬을 하겠다는 것입니다.

아래는 실제 JPA에서 작성한 Query 입니다. 쿼리문을 코드로 작성하기 때문에 실제로 어떤 쿼리가 동작하는지 반드시 확인하는 습관을 갖는 것이 좋습니다.

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
order by
    account0_.id desc limit ?

select
    count(account0_.id) as col_0_0_ 
from
    account account0_
```


## 개선
위의 `Pageable`의 개선할 점이 있습니다. 우선 `size`에 대한 limit이 없습니다. 위의 API에서 `size`값을 200000을 넘기면 실제 데이터베이스 쿼리문이 200000의 조회할 수 있습니다. 그 밖에 page가 0 부터 시작하는 것들도 개선하는 것이 필요해 보입니다.

### PageRequest

```java
public final class PageRequest {

    private int page;
    private int size;
    private Sort.Direction direction;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }
    // getter

    public org.springframework.data.domain.PageRequest of() {
        return org.springframework.data.domain.PageRequest.of(page -1, size, direction, "createdAt");
    }
```
`Pageable`을 대체하는 `PageRequest` 클래스를 작성합니다. 

* `setPage(int page)` 메서드를 통해서 0보다 작은 페이지를 요청했을 경우 1 페이지로 설정합니다.
* `setSize(int size)` 메서드를 통해서 요청 사이즈 50 보다 크면 기본 사이즈인 10으로 바인딩 합니다.
* `of()` 메서트를 통해서 `PageRequest` 객체를 응답해줍니다. 페이지는 0부터 시작하니 `page -1` 합니다. 본 예제에서는 sort는 `createdAt` 기준으로 진행합니다.

### 컨트롤러
```java
@RestController
@RequestMapping("accounts")
public class AccountController {
    ...

    @GetMapping
    public Page<AccountDto.Res> getAccounts(final PageRequest pageable) {
        return accountService.findAll(pageable.of()).map(AccountDto.Res::new);
    }
}
```
컨트롤러 영역은 간단합니다. `Pageable` -> `PageRequest` 교체하면 됩니다.

### 요청

![](/images/swagger-paging.png)

page를 기본 사이즈를 50을 넘는 500을 설정했습니다. `PageRequest`이 정상 동작한다면 기본 사이즈 10으로 설정해서 페이징 처리가 진행됩니다. 아래는 응답 값입니다.

```json
{
  "last": false,
  "totalPages": 2,
  "totalElements": 13,
  "size": 10, // size 값이 10으로 정상 작동
  "number": 0,
  "numberOfElements": 10,
  "first": true,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "empty": false
}
```

## 결론
Spring Data JPA는 페이징 처리는 데이터베이스마다 페이징 쿼리가 다른 부분들은 신경 쓰지 않고 더 쉽게 구현할 수 있는 장점이 있습니다. 또 `Pageable`을 통해서 페이징 구현이 가능하지만, 별도의 `Request` 객체를 두어서 관리하는 것이 쉽다고 생각합니다.
