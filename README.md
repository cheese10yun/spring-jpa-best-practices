# Spring-Jpa Best Practices [![Build Status](https://travis-ci.org/cheese10yun/spring-jpa.svg?branch=master)](https://travis-ci.org/cheese10yun/spring-jpa) [![Coverage Status](https://coveralls.io/repos/github/cheese10yun/spring-jpa/badge.svg?branch=master)](https://coveralls.io/github/cheese10yun/spring-jpa?branch=master)

최근 스프링을 6개월 가까이 하면서 제가 느낀 점들에 대해서 간단하게 정리했습니다. **아직 부족한 게 많아 Best Practices라도 당당하게 말하긴 어렵지만, 저와 같은 고민을 하시는 분들에게 조금이라도 도움이 되고 싶어 이렇게 정리했습니다.** 지속해서 해당 프로젝트를 이어 나아갈 예정이라 깃허브 Start, Watching 버튼을 누르시면 구독 신청받으실 수 있습니다. 저의 경험이 여러분에게 조금이라도 도움이 되기를 기원합니다.

## 프로젝트 실행환경

* Lombok이 반드시 설치 되있어야 합니다.
  - [Eclipse 설치 : [lombok] eclipse(STS)에 lombok(롬복) 설치](http://countryxide.tistory.com/16)
  - [Intell J 설치 : [Intellij] lombok 사용하기](http://blog.woniper.net/229)

### 실행
```
$ mvn spring-boot:run
```

### API Swagger
![](https://i.imgur.com/1cc1auF.png)
해당 API는 Swagger [http://localhost:8080/swagger-ui.html#/](http://localhost:8080/swagger-ui.html#/)으로 테스트해 볼 수 있습니다.



## [Step-01](https://github.com/cheese10yun/spring-jpa/blob/master/doc/step-01.md)

Spring Boot + JPA를 통해서 Account 생성, 조회, 수정 API를 간단하게 만드는 예제

### 중요 포인트
* [도메인 클래스 작성](https://github.com/cheese10yun/spring-jpa/blob/master/doc/step-01.md#%EB%8F%84%EB%A9%94%EC%9D%B8-%ED%81%B4%EB%9E%98%EC%8A%A4-%EC%9E%91%EC%84%B1--account-domain)
* [DTO 클래스를 이용한 Request, Response](https://github.com/cheese10yun/spring-jpa/blob/master/doc/step-01.md#dto-%ED%81%B4%EB%9E%98%EC%8A%A4%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-request-response)
* [Setter 사용안하기](https://github.com/cheese10yun/spring-jpa/blob/master/doc/step-01.md#setter-%EC%82%AC%EC%9A%A9%EC%95%88%ED%95%98%EA%B8%B0)
