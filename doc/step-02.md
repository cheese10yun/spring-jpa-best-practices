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
이전 단계에서 작성한 회원가입을 위한 SignUpReq.class에 새롭게 추가된 `@Email`, `@NotEmpty` 어노테이션을 추가했습니다. 보시다 싶이 해당 어노테이션은 유효성검사를 위한 것입니다. `@Valid` 어노테이션을 통해서 유효성 검사가를 진행하고 유효성 검사를 실패하면 `MethodArgumentNotValidException` 예외가 발생합니다.

### Controller
```java
@RequestMapping(method = RequestMethod.POST)
@ResponseStatus(value = HttpStatus.CREATED)
public AccountDto.Res signUp(@RequestBody @Valid final AccountDto.SignUpReq dto) {
    return new AccountDto.Res(accountService.create(dto));
}
```
컨트롤러에 `@Valid` 어 로테이션을 추가했습니다. `SignUpReq` 클래스의 유효성 검사가 실패했을 경우 `MethodArgumentNotValidException` 예외가 발생하게 됩니다. **프론트에서 넘겨받은 값에 대한 유효성 검사는 엄청난 반복적인 작업이며 실패했을 경우 사용자에게 적절한 Response 값을 리턴해주는 것 또한 중요 비즈니스 로직이 아님에도 불과하고 많은 시간을 할애하게 됩니다.** 다음 부분은 `MethodArgumentNotValidException` 발생시 공통적으로 **사용자에게 적절한 Response 값을 리턴해주는 작업을 진행하겠습니다.**


## ControllerAdvice

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
`@ControllerAdvice` 어노테이션을 추가하면 특정 Exception을 헨들링하여 적절한 값을 Response 값으로 리턴 해줍니다. 위처럼 별다른 `MethodArgumentNotValidException` 헨들링을 하지 않으면 스프링 자체의 에러 Response 값을 아래와 같이 리턴 해줍니다.

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
너무나 많은 값을을 리턴해주고 있으며 시스템 정보에 대한 값들도 포함되고 있어 위 처럼 Response 값을 리턴하는 것은 바람직하지 않습니다. 또 자체적으로 리턴해주는 Response 결과를 공통적인 포멧으로 가져가는 것 또한 아주 중요한 것이라고 생각합니다. 아래 `ErrorResponse` 클래스를 통해서 공통적인 예외 Response 값을 갖도록 하겠습니다.

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

### ErrorResponse
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






## 중점

* 공통적으로 처리
* 예외 리스트 배열로 던저주는거
* @Valid
* ErrorCode 코드
* DataIntegrityViolationException : 디비 무결정 조건
* MethodArgumentNotValidException : 넘겨온 값에 대한 발리데이션
* ConstraintViolationException : 최종 디비에 넣을때 작동하는 발리데이션


## 문제점

해당 클래스의 모두 발리데이션을 매번 넣어야함
