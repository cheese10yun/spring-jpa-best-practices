# Step-01 Account 생성, 조회, 수정 API를 간단하게 만드는 예제
Spring Boot + JPA를 활용한 Account 생성, 조회, 수정 API를 간단하게 만드는 예제입니다. 해당 코드는 [spring-jpa](https://github.com/cheese10yun/spring-jpa)를 확인해주세요.

## 중요 포인트
* 도메인 클래스 작성
* DTO 클래스를 이용한 Request, Response
* Setter 사용안하기

## 도메인 클래스 작성 : Account Domain
```java
@Entity
@Table(name = "account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    ...
    ...
    ...

    @Column(name = "zip", nullable = false)
    private String zip;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Builder
    public Account(String email, String fistName, String lastName, String password, String address1, String address2, String zip) {
        this.email = email;
        this.fistName = fistName;
        this.lastName = lastName;
        this.password = password;
        this.address1 = address1;
        this.address2 = address2;
        this.zip = zip;
    }

    public void updateMyAccount(AccountDto.MyAccountReq dto) {
        this.address1 = dto.getAddress1();
        this.address2 = dto.getAddress2();
        this.zip = dto.getZip();
    }
}
```
### 제약조건 맞추기
칼럼에 대한 제약조건을 생각하며 작성하는 하는 것이 바람직합니다. 대표적으로 `nullable`, `unique` 조건등 해당 디비의 스키마와 동일하게 설정하는 것이 좋습니다.

### 생성날짜, 수정날짜 값 설정 못하게 하기
기본적으로 `setter` 메서드가 모든 멤버 필드에 대해서 없고 생성자를 이용한 Builder Pattern 메서드에도 생성, 수정 날짜를 제외해 `@CreationTimestamp`, `@UpdateTimestamp` 어노테이션을 이용해서 VM시간 기준으로 날짜가 자동으로 입력하게 하거나 데이터베이스에서 자동으로 입력하게 설정하는 편이 좋습니다. 매번 생성할 때 create 시간을 넣어 주고, update 할 때 넣어 주고 반복적인 작업과 실수를 줄일 수 있는 효과적인 방법이라고 생각합니다.

### 객체 생성 제약
`@NoArgsConstructor(access = AccessLevel.PROTECTED)` lombok 어노테이션을 통해서 객체의 직접생성을 외부에서 못하게 설정하였습니다. 그래서  `@Builder` 에노티이션이 설정돼 있는 `Account` 생성자 메소드를 통해서 해당 객체를 생성할 수 있습니다. 이렇게 빌더 패턴을 이용해서 객체 생성을 강요하면 다음과 같은 장점이 있습니다. ( Account 생성자의 모든 인자값을 넣어주면 생성은 가능합니다.)

#### 객체를 유연하게 생성할 수 있습니다.
```java
Account.builder()
  .address1("서울")
  .address2("성동구")
  .zip("052-2344")
  .email("email")
  .fistName("yun")
  .lastName("kim")
  .password("password111")
  .build();
```
* 객체를 생성할 때 인자 값의 순서가 상관없습니다.
* 입력되는 값이 정확히 어떤 값인지 알 수 있습니다.
  - address1() 자연스럽게 address1에 입력되는 것을 알 수 있습니다.
* 하나의 생성자로 대체가 능합니다.
  - 여러 생성자를 두지 않고 하나의 생성자를 통해서 객체 생성이 가능합니다.

## DTO 클래스를 이용한 Request, Response

### DTO 클래스
```java
public class AccountDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUpReq {
        private String email;
        ...
        private String address2;
        private String zip;

        @Builder
        public SignUpReq(String email, String fistName, String lastName, String password, String address1, String address2, String zip) {
            this.email = email;
            ...
            this.address2 = address2;
            this.zip = zip;
        }

        public Account toEntity() {
            return Account.builder()
                    .email(this.email)
                    ...
                    .address2(this.address2)
                    .zip(this.zip)
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MyAccountReq {
        private String address1;
        private String address2;
        private String zip;

        @Builder
        public MyAccountReq(String address1, String address2, String zip) {
            this.address1 = address1;
            this.address2 = address2;
            this.zip = zip;
        }

    }

    @Getter
    public static class Res {
        private String email;
        ...
        private String address2;
        private String zip;

        public Res(Account account) {
            this.email = account.getEmail();
            ...
            this.address2 = account.getAddress2();
            this.zip = account.getZip();
        }
    }
}
```

### DTO 클래스의 필요 이유
Account에 정보를 변경하는 API가 있다고 가정 했을 경우 RequestBody를 Account 클래스로 받게 된다면 다음과 같은 문제가 발생합니다.

* 데이터 안전성
  - 정보 변경 API에서는 firstName, lastName 두 속성만 변경할 수 있다고 했으면 Account 클래스로 RequestBody를 받게 된다면 email, password, Account 클래의의 모든 속성값들을 컨트롤러를 통해서 넘겨받을 수 있게 되고 원치 않은 데이터 변경이 발생할 수 있습니다.
  - firstName, lastName 속성 이외의 값들이 넘어온다면 그것은 잘못된 입력값이고 그런 값들을 넘겼을 경우 Bad Request 처리하는 것이 안전합니다.
   - Response 타입이 Account 클래스일 경우 계정의 모든 정보가 노출 되게 됩니다. JsonIgnore 속성들을 두어 임시로 막는 것은 바람직하지 않습니다.속성들을 두어 임시로 막는 것은 바람직하지 않습니다.
* 명확해지는 요구사항
  - MyAccountReq 클래스는 마이 어카운트 페이지에서 변경할 수 있는 값들로 address1, address2, zip 속성이 있습니다. 요구사항이 이 세 가지 속성에 대한 변경이어서 해당 API가 어떤 값들을 변경할 수가 있는지 명확해집니다.

### 컨트롤러에서의 DTO

```java
@RequestMapping(method = RequestMethod.POST)
@ResponseStatus(value = HttpStatus.CREATED)
public AccountDto.Res signUp(@RequestBody final AccountDto.SignUpReq dto) {
    return new AccountDto.Res(accountService.create(dto));
}

@RequestMapping(value = "/{id}", method = RequestMethod.GET)
@ResponseStatus(value = HttpStatus.OK)
public AccountDto.Res getUser(@PathVariable final long id) {
    return new AccountDto.Res(accountService.findById(id));
}

@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
@ResponseStatus(value = HttpStatus.OK)
public AccountDto.Res updateMyAccount(@PathVariable final long id, @RequestBody final AccountDto.MyAccountReq dto) {
    return new AccountDto.Res(accountService.updateMyAccount(id, dto));
}
```
![](https://i.imgur.com/pbhdpcV.png)
위에서 언급했듯이 Request 값과 Response 값이 명확하게 되어 API 또 한 명확해집니다. 위 그림처럼 swagger API Document를 사용한다면 Request 값과 Response 자동으로 명세 되는 장점 또한 있습니다.



## Setter 사용안하기
JPA에서는 영속성이 있는 객체에서 Setter 메서드를 통해서 데이터베이스 DML이 가능하게 됩니다. 만약 무분별하게 모든 필드에 대한 Setter 메서드를 작성했을 경우 email 변경 기능이 없는 기획 의도가 있더라도 영속성이 있는 상태에서 Setter 메서드를 사용해서 얼마든지 변경이 가능해지는 구조를 갖게 됩니다. 또 굳이 변경 기능이 없는 속성뿐만이 아니라 영속성만 있으면 언제든지 DML이 가능한 구조는 안전하지 않다고 생각합니다. 또 데이터 변경이 발생했을 시 추적할 포인트들도 많아집니다. DTO 클래스를 기준으로 데이터 변경이 이루어진다면 명확한 요구사항에 의해서 변경이 된다고 생각합니다.

```java
// setter 이용 방법
public Account updateMyAccount(long id) {
    final Account account = findById(id);
    account.setAddress1("변경...");
    account.setAddress2("변경...");
    account.setZip("변경...");
    return account;
}
// Dto 이용 방법
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
  final Account account = findById(id);
  account.updateMyAccount(dto);
  return account;
}
// Account 클래스의 일부
public void updateMyAccount(AccountDto.MyAccountReq dto) {
  this.address1 = dto.getAddress1();
  this.address2 = dto.getAddress2();
  this.zip = dto.getZip();
}
```
DTO 클래스를 이용해서 데이터 변경을 하는 것이 훨씬더 직관적이고 유지보수 하기 쉽다고 생각합니다.  MyAccountReq 클래스에는 3개의 필드가 있으니 오직 3개의 필드만 변경이 가능하다는 것이 아주 명확해집니다.

여기서 제가 중요하다고 생각하는 것은 `updateMyAccount(AccountDto.MyAccountReq dto)` 메소드입니다. **객체 자신을 변경하는 것은 언제나 자기 자신이어야 한다는 OOP 관점에서 도메인 클래스에 updateMyAccount 기능이 있는 것이 맞는다고 생각합니다.**

## 마무리
최근 스프링을 6개월 가까이 하면서 제가 느낀 점들에 대해서 간단하게 정리했습니다. **아직 부족한 게 많아 Best Practices라도 당당하게 말하긴 어렵지만, 저와 같은 고민을 하시는 분들에게 조금이라도 도움이 되고 싶어 이렇게 정리했습니다.** 또 Step-02에서는 예외 처리와 유효성 검사에 대한 것을 정리할 예정입니다. 지속해서 해당 프로젝트를 이어 나아갈 예정이라 깃허브 start, watch 버튼을 누르시면 구독 신청받으실 수 있습니다. 저의 경험이 여러분에게 조금이라도 도움이 되기를 기원합니다.