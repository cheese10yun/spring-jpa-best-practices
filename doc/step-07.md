# step-07: Embedded를 적극 활용
Embedded을 사용하면 칼럼들을 자료형으로 규합해서 응집력 및 재사용성을 높여 훨씬 더 객체지향 프로그래밍을 할 수 있게 도울 수 있습니다. Embedded은 다음과 같은 장점들이 있습니다.


## 자료형의 통일
```java
class Account {
    // 단순 String
    @email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Email 자료형 
    @Embedded
    private Email email;
}

class Email {
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String value;
}
```
위처럼 단순 String 자료형에서 Email 자료형으로 통일이 됩니다. **자료형이 통일되면 많은 더욱 안전성이 높아지는 효과가 있습니다.**

```java
public Account findByEmail(final Email email) { //단순 문자열일 경우 (final String email)
    final Account account = accountRepository.findByEmail(email);
    if (account == null) throw new AccountNotFoundException(email);
    return account;
}
```
이메일로 회원을 조회 할 때 단순 문자열일 경우에는 굳이 이메일 형식을 맞추지 않고도 단순 문자열을 통해서 조회할 수 있습니다. 이것은 편하게 느껴질지는 모르나 안전성에는 좋다고 생각하지 않습니다. 위처럼 정확한 이메일 자료형으로 조회가 가능하게 안전성을 높일 수 있습니다. **위처럼 단순 조회용뿐만이 아니라 Email에 관련된 모든 자료형을 단순 String에서 Email로 변경함으로써 얻을 수 있는 이점은 많습니다.**


## 풍부한 객체 (Rich Obejct)

```java
public class Email {
    ...
    public String getHost() {
        int index = value.indexOf("@");
        return value.substring(index);
    }

    public String getId() {
        int index = value.indexOf("@");
        return value.substring(0, index);
    }
}
```
이메일 아이디와 호스트값을 추출해야 하는 기능이 필요해질 경우 기존 String 자로 형일 경우에는 해당 로직을 Account 도메인 객체에 추가하든, 유틸성 클래스에 추가하든 해야 합니다.

도메인 객체에 추가할 때는 Account 도메인 클래스가 갖는 책임들이 많아집니다. 또 이메일은 어디서든지 사용할 수 있는데 Account 객체에서 이 기능을 정의하는 것은 올바르지 않습니다.

유틸성 클래스에 추가하는 것 또한 좋지 않아 보입니다. 일단 유틸성 클래스에 해당 기능이 있는지 알아봐야 하고 기능이 있음에도 불구하고 그것을 모르고 추가하여 중복 코드가 발생하는 일이 너무나도 흔하게 발생합니다.

이것을 Email 형으로 빼놓았다면 아래처럼 Email 객체를 사용하는 곳 어디든지 사용할 수 있습니다. 해당 기능은 Email 객체가 해야 하는 일이고 또 그 일을 가장 잘할 수 있는 객체입니다. 또한 코드가 아주 이해하기 쉽게 됩니다. 객체의 기능이 풍부해집니다.

```java
email.getHost();
email.getId();
```

## 재사용성

가령 해외 송금을 하는 기능이 있다고 가정할 경우 Remittance 클래스는 보내는 금액, 나라, 통화, 받는 금액, 나라, 통화가 필요하다. 이처럼 도메인이 복잡해질수록 더 재사용성은 중요합니다.

```java
class Remittance {
    //자료형이 없는 경우
    @Column(name = "send_amount") private double sendAamount;
    @Column(name = "send_country") private String sendCountry;
    @Column(name = "send_currency") private String sendCurrency;

    @Column(name = "receive_amount") private double receiveAamount;
    @Column(name = "receive_country") private String receiveCountry;
    @Column(name = "receive_currency") private String receiveCurrency;

    //Money 자료형
    private Money snedMoney;
    private Money receiveMoney;
}
class Money {
    @Column(name = "amount", nullable = false, updatable = false) private double amount;
    @Column(name = "country", nullable = false, updatable = false) private Country country;
    @Column(name = "currency", nullable = false, updatable = false) private Currency currency;
}
```
위처럼 Money라는 자료형을 두고 금액, 나라, 통화를 두면 도메인을 이해하는데 한결 수월할 뿐만 아니라 수많은 곳에서 재사용 할 수 있습니다. 사용자에게 해당 통화로 금액을 보여줄 때 소숫자리 몇 자리로 보여줄 것인지 등등 핵심 도메인일수록 재사용성을 높여 중복 코드를 제거하고 응집력을 높일 수 있습니다.


## 결론
Embedded의 장점을 계속 이야기했습니다. 자료형을 통일해서 안전성 및 재사용성을 높이고 풍부한 객체를 갖게 함으로써 많은 장점을 얻을 수 있습니다. 이러한 장점들은 객체지향 프로그래밍에 충분히 나와 있는 내용입니다. 제가 하고 싶은 이야기는 **JPA는 결국 객체지향 프로그래밍을 돕는 도구** 라는 이야기입니다. 실제 데이터는 관계형 데이터베이스에 저장됨으로써 객체지향과 패러다임이 일치하지 않는 부분을 JPA는 너무나도 좋게 해결해줍니다. 그러니 JPA가 주는 다양한 어노테이션, 기능들도 좋지만 결국 이것이 궁극적으로 무엇을 위한 것인지 생각해보는 것도 좋다고 생각합니다.