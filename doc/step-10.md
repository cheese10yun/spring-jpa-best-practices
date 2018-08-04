# step-10: Properties 설정값 가져오기

Properties 설정값을 가져오는 다양한 방법들이 있습니다. 방법이 많다 보니 좋지 않은 패턴으로 사용하는 예도 흔하게 발생합니다. 패턴을 소개하고 이것이 왜 안 좋은지 간단하게 소개하고 제가 생각하는 좋은 패턴도 소개해드리겠습니다.


## properties
```yml
user:
  email: "yun@test.com"
  name : "yun"
  age: 27
  auth: true
  amount: 100
```
properties 설정은 위와 같습니다. 참고로 .yml 설정 파일입니다.

## 안티패턴 : Environment

```java
public class AntiSamplePropertiesRunner implements ApplicationRunner {
    private final Environment env;
    
    @Override
    public void run(ApplicationArguments args)  {
        final String email = env.getProperty("user.email");
        final String name = env.getProperty("user.name");
        final int age = Integer.valueOf(env.getProperty("user.age"));
        final boolean auth = Boolean.valueOf(env.getProperty("user.auth"));
        final int amount = Integer.valueOf(env.getProperty("user.amount"));

        log.info("=========ANTI=========");
        log.info(email); // "yun@test.com"
        log.info(name); // yun
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

## POJO

```java
@Configuration
@ConfigurationProperties(prefix = "user")
public class SampleProperties {
    private String email;
    private String name;
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
        final String name = properties.getName();
        final int age = properties.getAge();
        final boolean auth = properties.isAuth();
        final double amount = properties.getAmount();

        log.info("==================");
        log.info(email); // yun@test.com
        log.info(name); // yun
        log.info(String.valueOf(age)); // 27
        log.info(String.valueOf(auth)); // true
        log.info(String.valueOf(amount)); // 100.0
        log.info("==================");
    }
}
```
아주 간단하고 명확한 해결 방법은 POJO 객체를 두는 것입니다. 장점들은 다음과 같습니다. 

* 명확한 자료형을 알 수 있습니다.
* 변경시 포인트가 줄어듭니다.
* 재사용성이 높습니다.
* **POJO 클래스를 통해서 응집력을 높였습니다.**


## 결론
굳이 properties뿐만 아니라 많은 부분을 객체로 표현하는 것이 좋다고 생각합니다. 위에서 언급했다시피 개별적으로 user에 대한 데이터 email, name, age... 를 나열하는 것은 응집력을 심각하게 떨어트립니다. user가 가지고 있는 정보들이 가진 데이터들은 또 무엇이 있는지 확인하기 어렵고 캡슐화 또한 가 전혀 되지 않습니다. 

**위에서 설명한 부분을 properties의 한에서만 생각하지 않고 객체를 바라볼 때 데이터의 응집력, 캡슐화를 높이는 방법을 고민하는 것이 중요하다고 생각합니다.**