# step-05: OneToMany 관계 설정 팁

배송이 있고 배송의 상태를 갖는 배송 로그가 있고 각각의 관계는  1:N 관계 입니다. 이러한 경우 Delivery 클래스를 이용해서 DeliveryLog를 관리 하는 바람직합니다. 이러한 기준으로 1:N 관계에 대해서 정리해 보겠습니다.



## 요구사항??

* 배송이 있고 배송의 상태를 갖는 배송 로그가 있고 각각의 관계는  1:N 관계 이다.
* 배송 로그는 배송에 의해서 제어 되어야합니다.


## 관계 설정

```java
public class Delivery {

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeliveryLog> logs = new ArrayList<>();

    @Embedded
    private DateTime dateTime;
}

public class DeliveryLog {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = false)
    private DeliveryStatus status;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false, updatable = false)
    private Delivery delivery;

    @Embedded
    private DateTime dateTime;
}
```


@Embedded 타입으로 빼놓은 Address를 그대로 사용 했습니다. 이 처럼 핵심 도메인에 대해서 데이터의 연광성이 있는 것들을 Embedded 분리 해놓으면 여러모로 좋습니다.

또 DateTime 클래스도 Embedded 타입으로 지정해서 반복적인 생성일, 수정일 칼럼들을 일관성 있고 편리하게 생성할 수 있습니다.

**지금 부터는 1:N 관계 팁에 대한 이야기를 진행하겠습니다.**

* Delivery를 통해서 DeliveryLog를 관리함으로 `CascadeType.PERSIST` 설정을 주었습니다.
* 1: N 관계를 갖을 경우 List를 주로 사용하는데 객체 생성을 null로 설정하는 것보다  `new ArrayList<>();`설정 하는 것이 바람직합니다. 이유는 다음과 같습니다.

 ```java
 private void verifyStatus(DeliveryStatus status, Delivery delivery) {
     if (!delivery.getLogs().isEmpty()) {
     ...
     }
 }
 ```
* 초기화 하지 않았을 경우 null로 초기화 되며 ArrayList에서 지원해주는 함수를 사용할 수 없습니다. 1:N 관계에서 N이 없는 경우 null인 상태인 보다 Empty 상태가 훨씬 직관적입니다. null의 경우 값을 못가져 온것인지 값이 없는 것인지 의미가 분명하지 않습니다.

```java
public void addLog(DeliveryStatus status) {
    logs.add(DeliveryLog.builder()
            .status(status)
            .delivery(this)
            .build());
}
```
* CascadeType.PERSIST 설정을 주면  Delivery에서 DeliveryLog를 저장 시킬 수 있습니다. ArrayList 형으로 지정되 있다면 add 함수를 통해서 쉽게 저장할 수 있습니다. 이렇듯 ArrayList의 다양한 함수들을 사용 할 수 있습니다.

## 객체의 상태는 언제나 자기 자신이 관리합니다.

```java
public class DeliveryLog {
  @Id
  @GeneratedValue
  private long id;
  ....

  private void cancel() {
      verifyNotYetDelivering();
      this.status = DeliveryStatus.CANCELED;
  }

  private void verifyNotYetDelivering() {
      if (isNotYetDelivering()) throw new DeliveryAlreadyDeliveringException();
  }

  private void verifyAlreadyCompleted() {
    if (isCompleted())
        throw new IllegalArgumentException("It has already been completed and can not be changed.");
  }
}
```
객체의 상태는 언제나 자기 자신이 관리합니다. 즉 자신이 생성되지 못할 이유도 자기 자신이 관리 해야한다고 생각합니다. 위의 로직은 다음과 같습니다.
* cancel() : 배송을 취소 하기위해서는 아직 배달이 시작하기 이전의 상태어야 가능합니다.
* verifyAlreadyCompleted() : 마지막 로그가 COMPLETED 경우 더이상 로그를 기록할 수 없습니다.

즉 자신이 생성 할 수 없는 이유들은 자기 자신이 갖고 있어야 합니다. 이렇게 되면 어느곳에서 생성하든 동일한 기준으로 객채가 생성됩니다.

## 마무리
코드양이 많아 지고 있어서 반드시 전체 코드와 테스트 코드를 돌려 보면서 이해하는 것을 추천드립니다.
