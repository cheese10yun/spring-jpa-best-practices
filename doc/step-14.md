# step-14: JUnit5 적용하기

JUnit5는 다양한 어노테이션들이 추가되었습니다. 그중에 Junit5를 도입할 만큼 매력 있는 어노테이션 `@DisplayName` 입니다.

단순한 테스트 이외에는 테스트 코드 네이밍으로 테스트하고자 하는 의미를 전달하기가 매우 어렵습니다. 이때 아주 유용하게 사용할 수 있는 것이 `@DisplayName` 입니다.

![](/images/junit5-display-name.png)


위 그림처럼 `@DisplayName(....)`  어노테이션으로 코드에 대한 설명을 문자열로 대체할 수 있습니다. 이 대체된 문자열은 실제 테스트 케이스 이름으로 표시됩니다.

## 의존성 추가
Spring Boot2의 테스트코드 의존성은 JUnit4를 기본으로 가져오기 때문에 `spring-boot-starter-test` 의존성 이외에도 추가적인 작업이 필요합니다.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!--spring-boot-starter-test 의존성에서 가져오는 JUnit 제외 -->
    <exclusions>
        <exclusion>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<!--필요한 의존성 추가 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.3.2</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-runner</artifactId>
    <version>1.2.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.vintage</groupId>
    <artifactId>junit-vintage-engine</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
```

## 테스트 코드

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class AccountServiceJUnit5Test {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    @DisplayName("findById_존재하는경우_회원리턴")
    public void findBy_not_existed_test() {
        //given
        final AccountDto.SignUpReq dto = buildSignUpReq();
        given(accountRepository.findById(anyLong())).willReturn(Optional.of(dto.toEntity()));

        //when
        final Account account = accountService.findById(anyLong());

        //then
        verify(accountRepository, atLeastOnce()).findById(anyLong());
        assertThatEqual(dto, account);
    }
}
```
필요한 패키지의 경로가 중요하기 때문에 필요한 `import`을 추가했습니다. 아직 Spring Boot2에서 기본으로 가져온 의존성이 아니기 때문에 복잡한 부분이 있습니다. Prod 코드에는 Spring Boot2에서 JUnit5를 기본으로 택했을 때 변경하는 것이 더 안전하고 효율적이라고 생각합니다. 
