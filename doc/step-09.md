# step-09: OneToMany 관계 설정 팁(2)
이전에 OneToMany 관계 설정 포스팅이 관계설정의 초점보다는 풍부한 객체 관점 중심으로 다루었습니다. 그러다 보니 OneToMany에 대한 관계에 대한 설명 부분이 부족해서 추가 포스팅을 하게 되었습니다.

## 요구사항
* 배송이 이 있고 배송의 상태를 갖는 배송 로그가 있습니다.
* 배송과 배송 상태는 1:N 관계를 갖는다.
* 배송은 배송 상태를 1개 이상 반드시 갖는다.

## Entity
```java
@Entity
public class Delivery {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DeliveryLog> logs = new ArrayList<>();
}

@Entity
public class DeliveryLog {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, updatable = false)
    private DeliveryStatus status;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false, updatable = false)
    private Delivery delivery;
}
```

## Delivery 저장
일대다 관계에서는 다 쪽이 외래 키를 관리하게 됩니다. JPA 상에서는 왜래 키가 갖는 쪽이 연관 관계의 주인이 되고 연관 관계의 주인만이 데이터베이스 연관 관계와 매핑되고 왜래 키를 관리(등록, 수정, 삭제)할 수 있으므로 DeliveryLog에서 Delivery를 관리하게 됩니다. **하지만 DeliveryLog는 Delivery 상태를 저장하는 로그 성격 이기 때문에 핵심 비즈니스 로직을 Delivery에서 작성하는 것이 바람직합니다.**

이럴 때 편의 메소드와 Cascade 타입 PERSIST 이용하면 보다 이러한 문제를 해결 할 수 있습니다.

### 편의 메소드
```java
class Delivery {
    public void addLog(DeliveryStatus status) {
        this.logs.add(DeliveryLog.builder()
                .status(status)
                .delivery(this) // this를 통해서 Delivery를 넘겨준다.
                .build());
    }
}

class DeliveryLog {
    public DeliveryLog(final DeliveryStatus status, final Delivery delivery) {
        this.delivery = delivery;
    }
}

class DeliveryService {
    public Delivery create(DeliveryDto.CreationReq dto) {
        final Delivery delivery = dto.toEntity();
        delivery.addLog(DeliveryStatus.PENDING);
        return deliveryRepository.save(delivery);
    }
}
```
Delivery가 시작되면 DeliveryLog는 반드시 PENDDING이어야 한다고 가정했을 경우 편의 메소드를 이용해서 두 객체에 모두 필요한 값을 바인딩시켜줍니다.

### CaseCade PERSIST 설정
```sql
// cascade 없는 경우
Hibernate: insert into delivery (id, address1, address2, zip, created_at, update_at) values (null, ?, ?, ?, ?, ?)

// cascade PERSIST 설정 했을 경우
Hibernate: insert into delivery (id, address1, address2, zip, created_at, update_at) values (null, ?, ?, ?, ?, ?)
Hibernate: insert into delivery_log (id, created_at, update_at, delivery_id, status) values (null, ?, ?, ?, ?)
```
CaseCade PERSIST을 통해서 Delivery 엔티티에서 DeliveryLog를 생성할수 있게 설정합니다. CaseCade PERSIST가 없을 때 실제 객체에는 저장되지만, 영속성 있는 데이터베이스에 저장에 필요한 insert query가 동작하지 않습니다.

**JPA를 잘활용하면 도메인의 의도가 분명하게 들어나도록 개발할 수 있다는 것을 강조드리고 싶습니다.**

## 고아 객체 (orphanRemoval)
JPA는 부모 엔티티와 연관 관계가 끊어진 자식 엔티티를 자동으로 삭제하는 기능을 제공하는데 이것을 고아 객체 제거라 합니다. 이 기능을 사용해서 부모 엔티티의 컬렉션에서 자식 엔티티의 참조만 제거하면 자식 엔티티가 자동으로 삭제 돼서 개발의 편리함이 있습니다.

### DeliveryLog 삭제

```java
public Delivery removeLogs(long id) {
    final Delivery delivery = findById(id);
    delivery.getLogs().clear(); // DeloveryLog 전체 삭제
    return delivery; // 실제 DeloveryLog 삭제 여부를 확인하기 위해 리턴
}
```
```sql
// delete SQL
Hibernate: delete from delivery_log where id=?
```
Delivery 객체를 통해서 DeliveryLog를 위처럼 직관적으로 삭제 할 수 있습니다. 이 처럼 직관적으로 그 의도가 드러나는 장점이 있다고 생각합니다.

### Delivery 삭제

```java
public void remove(long id){
    deliveryRepository.delete(id);
}
```
```sql
// delete SQL
Hibernate: delete from delivery_log where id=?
Hibernate: delete from delivery where id=?
```
delivery, deliverylog 참조 관계를 맺고 있어 Delivery만 삭제할 수 없습니다. delete SQL을 보시다시피 delivery_log 부터 제거 이후 delivery를 제거하는 것을 알 수 있습니다. 이처럼 orphanRemoval 속성으로 더욱 쉽게 삭제 할 수 있습니다.

### orphanRemoval 설정이 없는 경우
DeliveryLog 삭제 같은 경우에는 실제 객체에서는 clear() 메서드로 DeliveryLog가 삭제된 것처럼 보이지만 영속성 있는 데이터를 삭제하는 것은 아니기에 해당 Delivery를 조회하면 DeliveryLog가 그대로 조회됩니다. 실수하기 좋은 부분이기에 반드시 삭제하고 조회까지 해서 데이터베이스까지 확인하시는 것을 권장해 드립니다.

![](https://i.imgur.com/bPhMX9e.png)

```
// erorr log
o.h.engine.jdbc.spi.SqlExceptionHelper   : Referential integrity constraint violation: "FKFS49KM0EA809MTH3OQ4S6810H: PUBLIC.DELIVERY_LOG FOREIGN KEY(DELIVERY_ID) REFERENCES PUBLIC.DELIVERY(ID) (1)"; SQL statement:
```
위에서 언급했듯이 delivery를 삭제하려면 참조 하는 deliverylog 먼저 삭제를 진행 해야 합니다. orphanRemoval 설정이 없는 경우 그 작업을 선행하지 않으니 위 같은 에러가 발생하게 됩니다.