# Appendix

## LocalDateTime 설정하기

### 클래스 설정
```java
@EntityScan(basePackageClasses = {Application.class, Jsr310JpaConverters.class}) //등록
@SpringBootApplication
public class RefactoringApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefactoringApplication.class, args);
    }
}
```
* 메인 클래스에서 설정
### Json 포메팅

```
 {
    "expirationDate": [ // 포멧팅전
          2018,
          12,
          12,
          0,
          0
        ],
 }
```

```josn
 {
    "expirationDate": "2018-12-12T00:00:00", // 변경후
 }
```


### 메이븐
```
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

### 프로퍼티 설정
```yml
spring:
  jackson:
      serialization:
        WRITE_DATES_AS_TIMESTAMPS: false
```

