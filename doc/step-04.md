# step-04 : Embedded를 이용한 Password 처리

이번 포스팅에서는 Embedded를 이용해서 Password 클래스를 통해서 Password 관련 응집력을 높이는 것 방법과  JPA에서 LocalDateTime을 활용하는 방법에 대해서 중점으로 포스팅을 진행해 보겠습니다.


## 중요포인트
* Embeddable 타입의 Password 클래스 정의


## Embeddable 타입의 Password 클래스 정의

### 비밀번호 요구사항
* 비밀번호 만료 기본 14일 기간이 있다.
* 비밀번호 만료 기간이 지나는 것을 알 수 있어야 한다.
* 비밀번호 5회 이상 실패했을 경우 더 이상 시도를 못하게 해야 한다.
* 비밀번호가 일치하는 경우 실패 카운트를 초기화 해야한다.
* 비밀번호 변경시 만료일이 현재시간 기준 14일로 연장되어야한다.


```java
@Embeddable
public class Password {
    @Column(name = "password", nullable = false)
    private String value;

    @Column(name = "password_expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "password_failed_count", nullable = false)
    private int failedCount;

    @Column(name = "password_ttl")
    private long ttl;

    @Builder
    public Password(final String value) {
        this.ttl = 1209_604; // 1209_604 is 14 days
        this.value = encodePassword(value);
        this.expirationDate = extendExpirationDate();
    }

    public boolean isMatched(final String rawPassword) {
        if (failedCount >= 5)
            throw new PasswordFailedExceededException();

        final boolean matches = isMatches(rawPassword);
        updateFailedCount(matches);
        return matches;
    }

    public void changePassword(final String newPassword, final String oldPassword) {
        if (isMatched(oldPassword)) {
            value = encodePassword(newPassword);
            extendExpirationDate();
        }
    }
}
```

**객체의 변경이나 질의는 반드시 해당 객체에 의해서 이루어져야 하는데 위의 요구 사항을 만족하는 로직들은 Password 객체 안에 있고 Password 객체를 통해서 모든 작업들이 이루어집니다.** 그래서 결과적으로 Password 관련 테스트 코드도 작성하기 쉬워지고 이렇게 작은 단위로 테스트 코드를 작성하면 실패했을 때 원인도 찾기 쉬워집니다.

결과적으로 Password의 책임이 명확해집니다. 만약 Embeddable 타입으로 분리하지 않았을 경우에는 해당 로직들은 모두 Account 클래스에 들어가 Account 책임이 증가하는 것을 방지할 수 있습니다.


## 소소한 팁
* 날짜 관련 클래스는 LocalDateTime 사용하였습니다. 설정 방법은 [링크](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/appendix-01.md)에서 확인해주세요
* LocalDateTime.now().plusSeconds(ttl); 현재 시간에서 시간 초만큼 더하는 함수입니다. 정말 직관적이며 다른 좋은 함수들이 있어 꼭 프로젝트에 도입해보시는 것을 추천드립니다.

## 결론
굳이 Password 에만 해당하는 경우가 아니라 핵심 도메인들을 Embeddable을 분리해서 책임을 분리하고 응집력, 재사용성을 높이는 것이 핵심 주제였습니다. 꼭 개인 프로젝트에서라도 핵심 도메인을 성격에 맞게끔 분리해 보시는 것을 경험해보시길 바랍니다.