# step-03 : 효과적인 validate, 예외 처리 처리 (2) (작업중...)

이전 포스팅의 단점을 해결해서 더 효과적은 효과적인 validate, 예외 처리 처리 작업을 진행해보겠습니다.

## [step-02 : 이전 포스팅의 단점](https://github.com/cheese10yun/spring-jpa/blob/master/doc/step-02.md)

1. 모든 Request Dto에 대한 반복적인 유효성 검사의 어노테이션이 필요합니다.
	- 회원 가입, 회원 정보 수정 등등 지속적으로 DTO 클래스가 추가되고 그때마다 반복적으로 어 로테이션이 추가됩니다.
2. 유효성 검사 로직이 변경되면 모든 곳에 변경이 따른다.
	- 만약 비밀번호 유효성 검사가 특수문자가 추가된다고 하면 비밀번호 변경에 따른 유효성 검사를 정규 표현식의 변경을 모든 DTO마다 해줘야 합니다.


## 중요포인트

* @Embeddable / @Embedded
* DTO 변경

## @Embeddable / @Embedded

### @Embeddable / @Embedded 적용

```java
public class Account {
    @Embedded
    private com.cheese.springjpa.Account.model.Email email;
}

@Embeddable
public class Email {

    @org.hibernate.validator.constraints.Email
    @Column(name = "email", nullable = false, unique = true)
    private String address;
}
```

임베디드 키워드를 통해서 새로운 값 타입을 집적 정의해서 사용할 수 있습니다. Email 클래스를 새로 생성하고 거기에 Email 칼럼에 매핑하는 하였습니다.

## DTO 변경

### AccountDto.class
```
public static class SignUpReq {

    // @Email 기존코드
    // private String email;
    @Valid // @Valid 반드시 필요
    private com.cheese.springjpa.Account.model.Email email;

    private String zip;
    @Builder
    public SignUpReq(com.cheese.springjpa.Account.model.Email email, String fistName, String lastName, String password, String address1, String address2, String zip) {
        this.email = email;
        ...
        this.zip = zip;
    }

    public Account toEntity() {
        return Account.builder()
                .email(this.email)
                ...
                .zip(this.zip)
                .build();
    }
}
```


모든 Request Dto에 대한 반복적인 유효성 검사의 어 로테이션이 필요했었지만 **새로운 Email 클래스를 바라보게 변경하면 해당 클래스의 이메일 유효성 검사를 바라보게 됩니다.**그 결과 이메일에 대한 유효성 검사는 Embeddable 타입의 Email 클래스가 관리하게 됩니다. 물론 이메일 유효성 검사는 로직이 거의 변경할 일이 없지만 다른 입력값들은 변경할 일들이 자주 생깁니다. 이럴 때 모든 DTO에 가서 유효성 로직을 변경하는 것은 불편 것을 넘어서 불안한 구조를 갖게 됩니다. 관리 포인트를 줄이는 것은 제가 생각했을 때는 되게 중요하다고 생각합니다.


## 단점
물론 이것 또한 단점이 없는 건 아닙니다. 아래 json처럼 email json 포멧팅이 변경되신 걸 확인할 수 있습니다. 물론 jackson을 사용해서 root element 조정을 할 수 있지만 그다지 추천해주고 싶지는 않습니다.
```json
{
  "address1": "string",
  "address2": "string",
  "email": {
    "address": "string"
  },
  "fistName": "string",
  "lastName": "string",
  "password": "string",
  "zip": "string"
}
```

## 결론
포스팅에는 유효성 검사를 하기 위해서 임베디드 타입을 분리했지만 사실 이런 이점보다는 다른 이점들이 많습니다. 또 이러한 이유로만 분리하지도 않는 걸로 알고 있습니다. 잘 설계된 ORM 애플리케이션은 매핑 한 테이블의 수보다 클래스의 수가 더 많다고들 합니다. 제가 생했을 때 진정한 장점은 다음과 같다고 생각합니다.

Account 엔티티는 fistName, lastName, password, address1, address2, zip 갖는 자입니다. 하지만 이러한 단순한 정보로 풀어 둔 것 일뿐. 데이터의 연관성이 없습니다. 아래처럼 정리하는 것이 더 바람직하다고 생각합니다.

Account 엔티티는 이름, 비밀번호, 주소를 갖는다. 여기에 필요한 상세 정보들은 주소라는 임베디드 타입에 정의돼있으면 된다고 생각합니다. 해당 설명을 json으로 풀어쓰면 아래와 같을 거같습니다.

```json
{
  "address": {
    "address1": "string",
    "address2": "string",
    "zip": "string"
  },
  "email": {
    "address": "string"
  },
  "name":{
    "first": "name",
    "last": "name"
  },
  "password": "string"
}
```
Account가 상세한 데이터를 그대로 가지고 있는 것은 객체지향적이지 않으며 응집 력만 떨어뜨리는 결과를 갖는다고 생각합니다. 저는 ORM JPA 기술은 단순히 반복적인 쿼리문을 대신 작성해주는 것이라고 생각하지는 않고 데이터를 데이터베이스에서만 생각하지 않고 그것을 객체지향 적으로 바라보게 결국 객체지향 프로그래밍을 돕는다고 생각합니다.


## 참고
* [자바 ORM 표준 JPA 프로그래밍 ](http://www.kyobobook.co.kr/product/detailViewKor.laf?ejkGb=KOR&mallGb=KOR&barcode=9788960777330&orderClick=LAH&Kc=)
