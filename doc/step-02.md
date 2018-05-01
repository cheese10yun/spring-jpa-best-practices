# Step-02

Spring Boot `@RestControllerAdvice`를 이용한 예외 처리  



* ErrorCode 코드
* DataIntegrityViolationException : 디비 무결정 조건
* MethodArgumentNotValidException : 넘겨온 값에 대한 발리데이션
* ConstraintViolationException : 최종 디비에 넣을때 작동하는 발리데이션


## 문제점

해당 클래스의 모두 발리데이션을 매번 넣어야함