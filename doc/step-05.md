# step-05: OneToMany 관계 설정 팁

배송이 있고 배송의 상태를 갖는 배송 로그가 있고 각각의 관계는 1:N 관계입니다. 아래와 같은 특정한 1:N 관계에 대해서 포스팅을 진행해보겠습니다.

## 배송 - 배송 로그
* 배송이 있고 배송의 상태를 갖는 배송 로그가 있습니다.
* 각각의 관계는 1:N 관계입니다.
* 다음과 같은 JSON을 갖습니다.
```json
{
  "address": {
    "address1": "서울 특별시...",
    "address2": "신림 ....",
    "zip": "020...."
  },
  "logs": [
    {
      "status": "PENDING"
    },
    {
      "status": "DELIVERING"
    },
    {
      "status": "COMPLETED"
    }
  ]
}
```
배송 로그는 단순히 배송의 상태를 표시하기 위한 것임으로 배송 엔티티에서 추가되는 것이 맞는다고 생각합니다. 위의 특성을 만족하는 관계 설정을 진행해보겠습니다.

## 관계 설정

```java
public class Delivery {

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeliveryLog> logs = new ArrayList<>();

    @Embedded
    private DateTime dateTime;
    ....
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

    ....
}
```
* @Embedded 타입으로 빼놓은 Address를 그대로 사용했습니다. 이처럼 핵심 도메인에 대해서 데이터의 연관성이 있는 것들을 Embedded 분리해놓으면 여러모로 좋습니다.
* DateTime 클래스도 Embedded 타입으로 지정해서 반복적인 생성일, 수정일 칼럼들을 일관성 있고 편리하게 생성할 수 있습니다.

**지금부터는 1:N 관계 팁에 관한 이야기를 진행하겠습니다.**

* Delivery를 통해서 DeliveryLog를 관리함으로 `CascadeType.PERSIST` 설정을 주었습니다.
* 1: N 관계를 맺을 경우 List를 주로 사용하는데 객체 생성을 null로 설정하는 것보다 `new ArrayList&amplt&ampgt();`설정하는 것이 바람직합니다. 이유는 다음과 같습니다.

 ```java
 private void verifyStatus(DeliveryStatus status, Delivery delivery) {
     if (!delivery.getLogs().isEmpty()) {
     ...
     }
 }
 ```
* 초기화하지 않았을 경우 null로 초기화되며 ArrayList에서 지원해주는 함수를 사용할 수 없습니다. 1:N 관계에서 N이 없는 경우 null인 상태인 보다 Empty 상태가 훨씬 직관적입니다. null의 경우 값을 못가져 온것인지 값이 없는 것인지 의미가 분명하지 않습니다.

```java
public void addLog(DeliveryStatus status) {
    logs.add(DeliveryLog.builder()
            .status(status)
            .delivery(this)
            .build());
}
```
* CascadeType.PERSIST 설정을 주면  Delivery에서 DeliveryLog를 저장시킬 수 있습니다. 이 때 ArrayList 형으로 지정돼 있다면 add 함수를 통해서 쉽게 저장할 수 있습니다. 이렇듯 ArrayList의 다양한 함수들을 사용할 수 있습니다.
* FetchType.EAGER 통해서 모든 로그 정보를 가져오고 있습니다. 로그 정보가 수십 개 이상일 경우는 Lazy 로딩을 통해서 가져오는 것이 좋지만 3~4개 정도로 가정했을 경우 FetchType.EAGER로 나쁘지 않다고 생각합니다.

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
객체의 상태는 언제나 자기 자신이 관리합니다. 즉 자신이 생성되지 못할 이유도 자기 자신이 관리해야 한다고 생각합니다. 위의 로직은 다음과 같습니다.
* cancel() : 배송을 취소하기 위해서는 아직 배달이 시작하기 이전의 상태여야 가능합니다.
* verifyAlreadyCompleted() : 마지막 로그가 COMPLETED 경우 더는 로그를 기록할 수 없습니다.

즉 자신이 생성할 수 없는 이유는 자기 자신이 갖고 있어야 합니다. 이렇게 되면 어느 곳에서 생성하든 같은 기준으로 객체가 생성되어 생성 관리 포인트가 한 곳에서 관리됩니다.

## 배송 로그 저장

```java
public class DeliveryService {

  public Delivery create(DeliveryDto.CreationReq dto) {
      final Delivery delivery = dto.toEntity();
      delivery.addLog(DeliveryStatus.PENDING);
      return deliveryRepository.save(delivery);
    }

  public Delivery updateStatus(long id, DeliveryDto.UpdateReq dto) {
    final Delivery delivery = findById(id);
    delivery.addLog(dto.getStatus());
    return delivery;
  }
}
```
* create : Delivery 클래스를 생성하고 delivery.addLog를 PENDING 상태로 생성하고 Repository의 save 메소드를 통해서 저장할 수 있습니다. 최종적인 JSON 값은 아래와 같습니다.

```json
{
  "address": {
    "address1": "서울 특별시...",
    "address2": "신림 ....",
    "zip": "020...."
  },
  "logs": [
    {
      "status": "PENDING"
    }
  ]
}
```
* updateStatus : 해당 객체를 데이터베이스에서 찾고 해당 배송 객체에 배송 로그를 추가합니다. 배송 로그에 추가적인 로그 저장은 `delivery.addLog(..);` 메서드를 통해서 진행됩니다. 언제나 관리 포인트를 줄이는 것은 중요하다고 생각됩니다.


## 마무리
코드 양이 많아지고 있어서 반드시 전체 코드와 테스트 코드를 돌려 보면서 이해하는 것을 추천해 드립니다. 이전 포스팅에서도 언급한 적 있지만 소스코드에서는 setter 메서드를 사용하지 않고 있습니다. 무분별하게 setter 메서드를 남용하는 것은 유지 보수와 가독성을 떨어트린다고 생각합니다. 다음 포스팅에서는 setter를 사용하지 않는 장점에 대해서 조금 더 깊게 설명해 보겠습니다.
