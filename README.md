[![Build Status](https://travis-ci.org/cheese10yun/spring-jpa-best-practices.svg?branch=master)](https://travis-ci.org/cheese10yun/spring-jpa-best-practices)
[![Coverage Status](https://coveralls.io/repos/github/cheese10yun/spring-jpa-best-practices/badge.svg?branch=master)](https://coveralls.io/github/cheese10yun/spring-jpa-best-practices?branch=master)
[![HitCount](http://hits.dwyl.io/cheese10yun/spring-jpa-best-practices.svg)](http://hits.dwyl.io/cheese10yun/spring-jpa-best-practices)

# Spring-Jpa Best Practices

스프링으로 개발을하면서 제가 느낀 점들에 대해서 간단하게 정리했습니다. **아직 부족한 게 많아 Best Practices라도 당당하게 말하긴 어렵지만, 저와 같은 고민을 하시는 분들에게 조금이라도 도움이 되고 싶어 이렇게 정리했습니다.** 지속해서 해당 프로젝트를 이어 나아갈 예정이라 깃허브 Start, Watching 버튼을 누르시면 구독 신청받으실 수 있습니다. 저의 경험이 여러분에게 조금이라도 도움이 되기를 기원합니다.


## 목차
1. [step-01 : Account 생성, 조회, 수정 API를 간단하게 만드는 예제](https://github.com/cheese10yun/spring-jpa/blob/master/doc/step-01.md)
2. [step-02 : 효과적인 validate, 예외 처리 (1)](https://github.com/cheese10yun/spring-jpa/blob/master/doc/step-02.md)
3. [step-03 : 효과적인 validate, 예외 처리 처리 (2)](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-03.md)
4. [step-04 : Embedded를 이용한 Password 처리](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-04.md)
5. [step-05: OneToMany 관계 설정 팁](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-05.md)
6. [step-06: Setter 사용하지 않기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-06.md)
7. [step-07: Embedded를 적극 활용](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-07.md)
8. [step-08: OneToOne 관계 설정 팁](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-08.md)
9. [step-09: OneToMany 관계 설정 팁(2)](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-09.md)
10. [step-10: Properties 설정값 가져오기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-10.md)
11. [step-11: Properties environment 설정하기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-11.md)
12. [step-12: 페이징 API 만들기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-12.md)
13. [step-13: Query Dsl이용한 페이징 API 만들기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-13.md)
14. [step-14: JUnit 5적용하기](https://github.com/cheese10yun/spring-jpa-best-practices/blob/master/doc/step-14.md)

**step-XX Branch 정보를 의미합니다. 보고 싶은 목차의 Branch로 checkout을 해주세요**


## 질문
![](https://i.imgur.com/Y4t4oWM.png)

* Github Issue를 통해서 이슈를 등록해주시면 제가 아는 부분에 대해서는 최대한 답변드리겠습니다.

## 개발환경
* Spring boot 1.5.8.RELEASE
* Java 8
* JPA & H2
* lombok
* maven

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
해당 API는 Swagger [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)으로 테스트해 볼 수 있습니다.
