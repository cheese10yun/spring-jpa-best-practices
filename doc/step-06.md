# step-06: Setter 사용하지 않기
객체지향 언어에서 관습처럼 setter를 추가하는 때도 있습니다. 무분별하게 setter를 사용하는 것은 바람직하지 않다고 생각합니다. 특히 도메인 객체들에는 더더욱이 말입니다. 이번 포스팅에서는 무분별한 setter의 단점과 setter를 이용하지 않고 도메인 객체를 변경하는 방법을 소개하겠습니다.


## Setter 메소드는 의도를 갖기 힘듭니다.

### Setter를 이용한 업데이트
```java
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
    final Account account = findById(id);
    account.setAddress("value");
    account.setFistName("value");
    account.setLastName("value");
    return account;
}
```
위의 코드는 회원 정보의 성, 이름, 주소를 변경하는 코드로 여러 setter 메소드들이 나열돼있습니다. 위 setter들은 회원 정보를 변경하기 위한 나열이라서 메소드들의 의도가 명확히 드러나지 않습니다.

### updateMyAccount 메서드를 이용한 업데이트
```java
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
    final Account account = findById(id);
    account.updateMyAccount(dto);
    return account;
}
// Account 도메인 클래스
public void updateMyAccount(AccountDto.MyAccountReq dto) {
    this.address = dto.getAddress();
    this.fistName = dto.getFistName();
    this.lastName = dto.getLastName();
}
```
Account 도메인 클래스에 updateMyAccount 메소드를 통해서 회원정보업데이트를 진행했습니다. 위의 코드보다 의도가 명확히 드러납니다.

```java
public static class MyAccountReq {
		private Address address;
		private String firstName;
		private String lastName;
}
```
위는 MyAccountReq 클래스입니다. 회원 정보 수정에 필요한 값 즉 변경될 값에 대한 명확한 명세가 있어 DTO를 두는 것이 바람직합니다.

### 객체의 일관성을 유지하기 어렵다
```java
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
    final Account account = findById(id);
    account.setEmail("value");
    return account;
}
```
setter 메소드가 있을 때 객체에 언제든지 변경할 수 있게 됩니다. 위처럼 회원 변경 메소드뿐만이 아니라 모든 곳에서 이메일 변경이 가능하게 됩니다. 물론 변경이 불가능 한 항목에 setter 메서드를 두지 않는다는 방법도 있지만 관례로 setter는 모든 멤버필드에 대해서 만들기도 하거니와 실수 조금이라도 덜 할 수 있게 하는 것이 바람직한 구조라고 생각합니다.

## Setter를 사용하지 않기

### updateMyAccount

```java
public Account updateMyAccount(long id, AccountDto.MyAccountReq dto) {
    final Account account = findById(id);
    account.updateMyAccount(dto);
    return account;
}
// Account 도메인 클래스
public void updateMyAccount(AccountDto.MyAccountReq dto) {
    this.address = dto.getAddress();
    this.fistName = dto.getFistName();
    this.lastName = dto.getLastName();
}
```
위의 예제와 같은 예제 코드입니다. findById 메소드를 통해서 영속성을 가진 객체를 가져오고 도메인에 작성된 updateMyAccount를 통해서 업데이트를 진행하고 있습니다.

**repository.save() 메소드를 사용하지 않았습니다. 다시 말해 메소드들은 객체 그 자신을 통해서 데이터베이스 변경작업을 진행하고, create 메서드에 대해서만 repository.save()를 사용합니다**

### create
```java
// 전체 코드를 보시는 것을 추천드립니다.
public static class SignUpReq {

	private com.cheese.springjpa.Account.model.Email email;
	private Address address;

	@Builder
	public SignUpReq(Email email, String fistName, String lastName, String password, Address address) {
        this.email = email;
        this.address = address;
	}

	public Account toEntity() {
        return Account.builder()
            .email(this.email)
            .address(this.address)
            .build();
	}
}

public Account create(AccountDto.SignUpReq dto) {
    return accountRepository.save(dto.toEntity());
}
```
setter 메소드 없이 create 하는 예제입니다. SignUpReq 클래스는 Request DTO 클래스를 통해서 사용자에게 필요한 값을 입력받고 그 값을 toEntity 메소드를 통해서 Account 객체를 생성하게 됩니다. 이 때 빌더 패턴을 이용해서 객체를 생성했습니다. 도메인 객체를 생성할 때 빌더패턴을 적극 추천해 드립니다. 빌더 패턴에 대해서는 여기서는 별도로 다루지 않겠습니다.

save 메소드에는 도메인 객체 타입이 들어가야 합니다. 이때 toEntity 메소드를 통해서 해당 객체로 새롭게 도메인 객체가 생성되고 save 메소드를 통해서 데이터베이스에 insert 됩니다.
