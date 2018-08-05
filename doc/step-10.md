# step-10: Properties 설정

Properties 설정값을 가져오는 다양한 방법들이 있습니다. 방법이 많다 보니 좋지 않은 패턴으로 사용하는 예도 흔하게 발생합니다. 패턴을 소개하고 이것이 왜 안 좋은지 간단하게 소개하고 제가 생각하는 좋은 패턴도 소개해드리겠습니다.


## properties
```yml
user:
  email: "yun@test.com"
  nickname: "yun"
  age: 28
  auth: false
  amount: 101
```
properties 설정은 위와 같습니다. 참고로 .yml 설정 파일입니다.

## 안티패턴 : Environment

```java
public class AntiSamplePropertiesRunner implements ApplicationRunner {
    private final Environment env;
    
    @Override
    public void run(ApplicationArguments args)  {
        final String email = env.getProperty("user.email");
        final String nickname = env.getProperty("user.nickname");
        final int age = Integer.valueOf(env.getProperty("user.age"));
        final boolean auth = Boolean.valueOf(env.getProperty("user.auth"));
        final int amount = Integer.valueOf(env.getProperty("user.amount"));

        log.info("=========ANTI=========");
        log.info(email); // "yun@test.com"
        log.info(nickname); // yun
        log.info(String.valueOf(age)); // 27
        log.info(String.valueOf(auth)); // true
        log.info(String.valueOf(amount)); // 100
        log.info("=========ANTI=========");
    }
}
```
일반적으로 가장 쉬운 Environment를 활용한 방법입니다. 많은 것들 생각하지 않고 properties에 정의된 것을 key 값으로 찾아옵니다. 

위의 Environment 이용해서 properties에서 설정을 가져오는 것은 편하지만 단점들이 있습니다.

### 정확한 자료형 확인의 어려움
key 값으로 어느 정도 유추할 수 있지만 어디까지나 유추이지 정확한 자료형을 확인하기 위해서는 properties에서 value 값을 기반으로 자료형을 확인해야 합니다. 또 amount 값이 100 이기 때문에 int 타입으로 바인딩시켰지만 amount 값은 소수로 값이 변경될 수도 있습니다. 이 또한 값을 통해서 자료형을 유추했기 때문에 발생한다고 생각합니다.

### 변경시 관리의 어려움
email의 키값이 email-address로 변경됐을시 getProperty() 메서드를 통해서 바인딩 시킨 부분들은 모두 email-address로 변경해야 합니다. 변경하는 것도 문제지만 만약 1개의 메소드라도 실수로 놓쳤을 경우 에러 발생 시점에 runtime으로 넘어가게 되고 해당 에러가 NullPointException이 발생하기 전까지는 확인하기 어렵습니다.

## 추천 패턴 : ConfigurationProperties

```java
@Configuration
@ConfigurationProperties(prefix = "user")
@Validated
public class SampleProperties {
    @Email
    private String email;
    @NotEmpty
    private String nickname;
    private int age;
    private boolean auth;
    private double amount;

    // getter, setter
}

public class SamplePropertiesRunner implements ApplicationRunner {
    private final SampleProperties properties;
    @Override
    public void run(ApplicationArguments args)  {
        final String email = properties.getEmail();
        final String nickname = properties.getNickname();
        final int age = properties.getAge();
        final boolean auth = properties.isAuth();
        final double amount = properties.getAmount();

        log.info("==================");
        log.info(email); // yun@test.com
        log.info(nickname); // yun
        log.info(String.valueOf(age)); // 27
        log.info(String.valueOf(auth)); // true
        log.info(String.valueOf(amount)); // 100.0
        log.info("==================");
    }
}
```
아주 간단하고 명확한 해결 방법은 ConfigurationProperties를 이용해서 POJO 객체를 두는 것입니다. 장점들은 다음과 같습니다. 

### Validation 

```yml
user:
  email: "yun@" // 이메일 형식 올바르지 않음 -> @Email
  nickname: "" // 필수 값 -> @NotEmpty
```
JSR-303 기반으로 Validate 검사를 할 수 있습니다. 위 코드 처럼 `@Validated`, `@Email` 어노테이션을 이용하면 쉽게 유효성 검사를 할 수 있습니다.

```bash

Binding to target com.cheese.springjpa.properties.SampleProperties$$EnhancerBySpringCGLIB$$68016904@3cc27db9 failed:

    Property: user.email
    Value: yun@
    Reason: 이메일 주소가 유효하지 않습니다.

Binding to target com.cheese.springjpa.properties.SampleProperties$$EnhancerBySpringCGLIB$$d2899f85@3ca58cc8 failed:

    Property: user.nickname
    Value: 
    Reason: 반드시 값이 존재하고 길이 혹은 크기가 0보다 커야 합니다.
```

**위와 같이 컴파일 과정 중에 잡아주고 에러 메시지도 상당히 구체적입니다.**

### 빈으로 등록 해서 재사용성이 높음

```java
public class SampleProperties {
    @Email
    private String email;
    @NotEmpty
    private String nickname;
    private int age;
    private boolean auth;
    private double amount;

    // getter, setter 
    // properties 사용할 떄는 SampleProperties 객체를 사용함, 데이터의 응집력, 캡슐화가 높아짐
}
```

SamplePropertiesRunner 클래스를 보시면 SampleProperties를 의존성 주입을 받아서 다른 빈에서 재사용성이 높습니다. 단순히 재사용성이 높은 것이 아니라 user의 응집력이 높아집니다.

개별적으로 user에 대한 데이터 email, nickname, age...를 나열하는 것은 응집력을 심각하게 떨어트린다고 생각합니다. user가 가지고 있는 정보들은 또 무엇인지 확인 하기 어렵고 정확한 타입을 유추하기도 어렵습니다. 이로 인해서 캡슐화의 심각한 저하로 이어 집니다..

### 그밖에 장점들
Relaxed Binding으로 properties 키값에 유연하게 지정할 수 있습니다.

SampleProperties에 firstName 추가되었을 때 바인딩시킬 properties 키값을 first-name, FIRSTNAME, firstName 등을 사용해도 바인딩이 됩니다. 장점이긴 하지만 반드시 하나의 네이밍을 정하고 통일하는 게 바람직하다고 생각합니다.



## 결론
**위에서 설명한 부분을 properties의 한에서만 생각하지 않고 객체를 바라볼 때 데이터의 응집력, 캡슐화를 높이는 방법을 고민하는 것이 중요하다고 생각합니다.**

## 참고 자료
* [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/)