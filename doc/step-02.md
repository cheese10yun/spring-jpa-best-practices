# Step-02

API을 개발하다 보면 프런트에서 넘어온 값에 대한 유효성 검사를 수없이 진행하게 됩니다. 이러한 **반복적인 작업을 보다 효율적으로 처리하고 정확한 예외 메시지를 프런트엔드에게 전달해주는 것이 목표입니다**.


## 중요 포인트

* `@Valid`를 통한 유효성검사
* `@ControllerAdvice`를 이용한 Exception 헨들링
* `ErrorCode` 에러 메시지 통합

## @Valid 를 통한 유효성검사

### DTO
```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public static class SignUpReq {
	@Email
	private String email;
	@NotEmpty
	private String fistName;
	...
	@NotEmpty
	private String zip;
}
```
이전 단계에서 작성한 회원가입을 위한 SignUpReq.class에 새롭게 추가된 `@Email`, `@NotEmpty` 어 로테이션을 추가했습니다. 보시다 싶이 해당 어 로테이션은 유효성 검사를 위한 것입니다. `@Valid` 어 로테이션을 통해서 유효성 검사 가를 진행하고 유효성 검사를 실패하면 `MethodArgumentNotValidException` 예외가 발생합니다.

### Controller
```java
@RequestMapping(method = RequestMethod.POST)
@ResponseStatus(value = HttpStatus.CREATED)
public AccountDto.Res signUp(@RequestBody @Valid final AccountDto.SignUpReq dto) {
    return new AccountDto.Res(accountService.create(dto));
}
```
컨트롤러에 `@Valid` 어 로테이션을 추가했습니다. `SignUpReq` 클래스의 유효성 검사가 실패했을 경우 `MethodArgumentNotValidException` 예외가 발생하게 됩니다. **프론트에서 넘겨받은 값에 대한 유효성 검사는 엄청난 반복적인 작업이며 실패했을 경우 사용자에게 적절한 Response 값을 리턴해주는 것 또한 중요 비즈니스 로직이 아님에도 불과하고 많은 시간을 할애하게 됩니다.** 다음 부분은 `MethodArgumentNotValidException` 발생시 공통적으로 **사용자에게 적절한 Response 값을 리턴해주는 작업을 진행하겠습니다.**


## @ControllerAdvice를 이용한 Exception 헨들링

```Java
@ControllerAdvice
public class ErrorExceptionController {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
	    retrun errorResponse...
	}
}
```
`@ControllerAdvice` 어 로테이션을 추가하면 특정 Exception을 핸들링하여 적절한 값을 Response 값으로 리턴해줍니다. 위처럼 별다른 `MethodArgumentNotValidException` 핸들링을 하지 않으면 스프링 자체의 에러 Response 값을 아래와 같이 리턴해줍니다.

### Error Response
```json
{
  "timestamp": 1525182817519,
  "status": 400,
  "error": "Bad Request",
  "exception": "org.springframework.web.bind.MethodArgumentNotValidException",
  "errors": [
    {
      "codes": [
        "Email.signUpReq.email",
        "Email.email",
        "Email.java.lang.String",
        "Email"
      ],
      "arguments": [
        {
          "codes": [
            "signUpReq.email",
            "email"
          ],
          "arguments": null,
          "defaultMessage": "email",
          "code": "email"
        },
        [],
        {
          "arguments": null,
          "defaultMessage": ".*",
          "codes": [
            ".*"
          ]
        }
      ],
      "defaultMessage": "이메일 주소가 유효하지 않습니다.",
      "objectName": "signUpReq",
      "field": "email",
      "rejectedValue": "string",
      "bindingFailure": false,
      "code": "Email"
    }
  ],
  "message": "Validation failed for object='signUpReq'. Error count: 3",
  "path": "/accounts"
}
```
너무나 많은 값을 돌려보내 주고 있으며 시스템 정보에 대한 값들도 포함되고 있어 위처럼 Response 값을 돌려보내는 것은 바람직하지 않습니다. 또 자체적으로 돌려보내 주는 Response 결과를 공통적인 포맷으로 가져가는 것은 최종적으로 프론트 엔드에서 처리해야 하므로 항상 공통적인 Response 포맷일 유지해야 합니다. 아래 `Error Response` 클래스를 통해서 공통적인 예외 Response 값을 갖도록 하겠습니다.

### MethodArgumentNotValidException의 Response 처리

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
protected ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error(e.getMessage());
		final BindingResult bindingResult = e.getBindingResult();
		final List<FieldError> errors = bindingResult.getFieldErrors();

		return buildFieldErrors(
						ErrorCode.INPUT_VALUE_INVALID,
						errors.parallelStream()
										.map(error -> ErrorResponse.FieldError.builder()
														.reason(error.getDefaultMessage())
														.field(error.getField())
														.value((String) error.getRejectedValue())
														.build())
										.collect(Collectors.toList())
		);
}
```
### ErrorResponse
```Java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String message;
    private String code;
    private int status;
    private List<FieldError> errors;
		...

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;
				...
    }
}
```

전체적인 흐름을 보시는 것을 권장합니다. 대충 소스코드의 흐름은 MethodArgumentNotValidException 클래스의 유효성 예외 부분들을 `ErrorResponse` 클래스의 정보에 알맞게 넣어주는 것입니다.

### ErrorResponse :  공통적인 예외 Response
```json
{
  "message": "입력값이 올바르지 않습니다.",
  "code": "???",
  "status": 400,
  "errors": [
    {
      "field": "email",
      "value": "string",
      "reason": "이메일 주소가 유효하지 않습니다."
    },
    {
      "field": "lastName",
      "value": null,
      "reason": "반드시 값이 존재하고 길이 혹은 크기가 0보다 커야 합니다."
    },
    {
      "field": "fistName",
      "value": null,
      "reason": "반드시 값이 존재하고 길이 혹은 크기가 0보다 커야 합니다."
    }
  ]
}
```
동일한 ErrorResponse 값을 갖게 되었으며 어느 칼럼에서 무슨 무슨 문제들이 발생했는지 알 수 있게 되었습니다. `@Valid` 어 로테이션으로 발생하는 `MethodArgumentNotValidException`들은 모두 handleMethodArgumentNotValidException 메서드를 통해서 공통된 Response 값을 리턴합니다. **이제부터는 @Valid, 해당 필드에 맞는 어 로테이션을 통해서 모든 유효성 검사를 진행할 수 있습니다.**



### AccountNotFoundException : 새로운 Exception 정의

```java
public class AccountNotFoundException extends RuntimeException {
    private long id;

    public AccountNotFoundException(long id) {
        this.id = id;
    }
}

public Account findById(long id) {
    final Account account = accountRepository.findOne(id);
    if (account == null)
        throw new AccountNotFoundException(id);
    return account;
}
```

### handleAccountNotFoundException : 헨들링
```java
@ExceptionHandler(value = {
        AccountNotFoundException.class
})
@ResponseStatus(HttpStatus.NOT_FOUND)
protected ErrorResponse handleAccountNotFoundException(AccountNotFoundException e) {
    final ErrorCode accountNotFound = ErrorCode.ACCOUNT_NOT_FOUND;
    log.error(accountNotFound.getMessage(), e.getMessage());
    return buildError(accountNotFound);
}
```

### Response
```json
{
  "message": "해당 회원을 찾을 수 없습니다.",
  "code": "AC_001",
  "status": 404,
  "errors": []
}
```
위처럼 새로운 Exception 정의하고 핸들링할 수 있습니다. 이 또 한 공통된 Response 갖게 되며 예외가 발생했을 경우 throw를 통해 해당 Exception 잘 처리해 주는 곳으로 던지게 됨으로써 비즈니스 로직과 예외 처리를 하는 로직이 분리되어 코드 가속성 및 유지 보수에 좋다고 생각합니다.

## ErrorCode
```Java
@Getter
public enum ErrorCode {

    ACCOUNT_NOT_FOUND("AC_001", "해당 회원을 찾을 수 없습니다.", 404),
    EMAIL_DUPLICATION("AC_002", "이메일이 중복되었습니다.", 400),
    INPUT_VALUE_INVALID("CM_001", "입력값이 올바르지 않습니다.", 400);

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
```
위 방법은 깃허브에서 많은 개발자들이 예외 처리를 하는 방법들의 장점들을 합쳐서 만든 방법이지만 이 에러 코드는 저의 생각으로만 만들어진 방법이라서 효율적인 방법인지는 아직 잘 모르겠습니다. 우선 각각 모두 흩어져있는 예외 메시지들을 한 곳에서 관리하는 것이 바람직하다고 생각합니다. 그 이유는 다음과 같습니다.

1. 중복적으로 작성되는 메시지들이 너무 많습니다.
- 예를 들어 `해당 회원을 찾을 수 없습니다.` 메시지를 로그에 남기는 메시지 형태는 너무나도 많은 형태입니다.
2. 메시지 변경이 힘듭니다.
- 메시지가 스트링 형식으로 모든 소스에 흩어져있을 경우 메시지 변경 시에 모든 곳을 다 찾아서 변경해야 합니다.


## 단점
위의 유효성 검사의 단점은 다음과 같습니다.

1. 모든 Request Dto에 대한 반복적인 유효성 검사의 어 로테이션이 필요합니다.
- 회원 가입, 회원 정보 수정 등등 지속적으로 DTO 클래스가 추가되고 그때마다 반복적으로 어 로테이션이 추가됩니다.
2. 유효성 검사 로직이 변경되면 모든 곳에 변경이 따른다.
- 만약 비밀번호 유효성 검사가 특수문자가 추가된다고 하면 비밀번호 변경에 따른 유효성 검사를 정규 표현식의 변경을 모든 DTO마다 해줘야 합니다.

이러한 단점들은 다음 `step-03 : 효과적인 validate, 예외 처리 처리 (2)`에서 다루어 보겠습니다. 지속적으로 포스팅이 어 가겠습니다. 긴 글 읽어주셔서 감사합니다.
