# step-08: OneToOne 관계 설정 팁

OneToOne 관계 설정 시에 간단한 팁을 정리하겠습니다. 해당 객체들의 성격은 다음과 같습니다. 

* 주문과 쿠폰 엔티티가 있다.
* 주문 시 쿠폰을 적용해서 할인받을 수 있다.
* 주문과 쿠폰 관계는 1:1 관계 즉 OneToOne 관계이다.

주의 깊게 살펴볼 내용은 다음과 같습니다.

* 외래 키는 어디 테이블에 두는 것이 좋은가?
* 양방향 연관 관계 편의 메소드
* 제약 조건으로 인한 안정성 및 성능 향상

## Entity 객체
```java
public class Coupon {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "discount_amount")
    private double discountAmount;

    @Column(name = "use")
    private boolean use;

    @OneToOne()
    private Order order;
}

public class Order {
    @Id
    @GeneratedValue
    private long id;
    
    @Column(name = "price")
    private double price;

    @OneToOne
    @JoinColumn()
    private Coupon coupon;
}
```


## 외래 키는 어디 테이블에 두는 것이 좋은가?

```java
// Order가 연관관계의 주인일 경우
@OneToOne
@JoinColumn(name = "coupon_id", referencedColumnName = "id")
private Coupon coupon;

@OneToOne(mappedBy = "coupon")
private Order order;

// coupon이 연관관계의 주인일 경우 
@OneToOne(mappedBy = "order")
private Coupon coupon;

@OneToOne
@JoinColumn(name = "order_id", referencedColumnName = "id")
private Order order;
```

일대다 관계에서는 다 쪽에서 외래 키를 관리 하게 되지만 상대적으로 일대일 관계 설정에는 외래 키를 어느 곳에 두어야 하는지를 생각을 해야 합니다. JPA 상에서는 외래 키가 갖는 쪽이 연관 관계의 주인이 되고 
**연관 관계의 주인만이 데이터베이스 연관 관계와 매핑되고 외래 키를 관리(등록, 수정, 삭제)할 수 있기 때문입니다.**

## Sample Code

```java
// 주문시 1,000 할인 쿠폰을 적용해본 간단한 코드입니다. 
public Order order() {
    final Order order = Order.builder().price(10_000).build(); // 10,000 상품주문
    Coupon coupon = couponService.findById(1); // 1,000 할인 쿠폰
    order.applyCoupon(coupon);
    return orderRepository.save(order);
}
@Test
public void order_쿠폰할인적용() {
    final Order order = orderService.order();
    assertThat(order.getPrice(), is(9_000D)); // 1,000 할인 적용 확인

    final Order findOrder = orderService.findOrder(order.getId());
    System.out.println("couponId : "+ findOrder.getCoupon().getId()); // couponId : 1 (coupon_id 외래 키를 저장 완료)
}
```


### Order가 주인일 경우 장점 : INSERT SQL이 한번 실행

![](https://i.imgur.com/k6V64ye.png)

```sql
// order가 연관 관계의 주인일 경우 SQL
insert into orders (id, coupon_id, price) values (null, ?, ?) 

//coupon이 연관 관계의 주인일 경우 SQL
insert into orders (id, price) values (null, ?)
update coupon set discount_amount=?, order_id=?, use=? where id=?
```
order 테이블에 coupon_id 칼럼을 저장하기 때문에 주문 SQL은 한 번만 실행됩니다. 반면에 coupon이 연관 관계의 주인일 경우에는 coupon에 order의 외래 키가 있으니 order INSERT SQL 한 번, coupon 테이블에 order_id 칼럼 업데이트 쿼리 한번 총 2번의 쿼리가 실행됩니다. 

작은 장점으로는 데이터베이스 칼럼에 coupon_id 항목이 null이 아닌 경우 할인 쿠폰이 적용된 것으로 판단할 수 있습니다.

### Order가 주인일 경우 단점 : 연관 관계 변경 시 취약
기존 요구사항은 주문 한 개에 쿠폰은 한 개만 적용이 가능 했기 때문에 OneToOne 연관 관계를 맺었지만 **하나의 주문에 여러 개의 쿠폰이 적용되는 기능이 추가되었을 때 변경하기 어렵다는 단점이 있습니다.** 

order 테이블에 coupon_id 칼럼을 갖고 있어서 여러 개의 쿠폰을 적용하기 위해서는 coupon 테이블에서 order_id 칼럼을 가진 구조로 변경 해야 합니다. **OneToMany 관계에서는 연관 관계의 주인은 외래 키를 갖는 엔티티가 갖는 것이 바람직합니다.** 비즈니스 로직 변경은 어려운 게 없으나 데이터베이스 칼럼들을 이전 해야 하기 때문에 실제 서비스 중인 프로젝트에는 상당히 골치 아프게 됩니다.

장점이 단점이 되고 단점이 장점이 되기 때문에 Coupon 장단점을 정리하지 않았습니다.

## 연관 관계의 주인 설정
OneToOne 관계를 맺으면 외래 키를 어디에 둘 것인지, 즉 연관 관계의 주인을 어디에 둘 것인지는 많은 고민이 필요 합니다. 제 개인적인 생각으로는 OneToMany로 변경될 가능성이 있는지를 판단하고 변경이 될 가능성이 있다고 판단되면 Many가 될 엔티티가 관계의 주인이 되는 것이 좋다고 봅니다. 또 애초에 OneToMany를 고려해서 초기 관계 설정을 OneToMany로 가져가는 것도 좋다고 생각합니다. 

그러니 이 연관 관계가 정말 OneToOne 관계인지 깊은 고민이 필요하고 해당 도메인에 대한 지식도 필요 하다고 생각합니다. 예를 들어 개인 송금 관계에서 입금 <-> 출금 관계를 가질 경우 반드시 하나의 입금 당 하나의 출금을 갖게 되니 이것은 OneToOne 관계로 맺어가도 무리가 없다고 판단됩니다. (물론 아닌 때도 있습니다. 그래서 해당 도메인에 대한 지식이 필요 한다고 생각합니다)

**주인 설정이라고 하면 뭔가 더 중요한 것이 주인이 되어야 할 거 같다는 생각이 들지만 연관 관계의 주인이라는 것은 외래 키의 위치와 관련해서 정해야 하지 해당 도메인의 중요성과는 상관관계가 없습니다.**

## 양방향 연관관계 편의 메소드

```java
// Order가 연관관계의 주인일 경우 예제
class Coupon {
    ...
    // 연관관계 편의 메소드
    public void use(final Order order) {
        this.order = order;
        this.use = true;
    }
}

class Order {
    private Coupon coupon; // (1)
    ...
    // 연관관계 편의 메소드
    public void applyCoupon(final Coupon coupon) {
        this.coupon = coupon;
        coupon.use(this);
        price -= coupon.getDiscountAmount();
    }
}

// 주문 생성시 1,000 할인 쿠폰 적용
public Order order() {
    final Order order = Order.builder().price(10_000).build(); // 10,000 상품주문
    Coupon coupon = couponService.findById(1); // 1,000 할인 쿠폰
    order.applyCoupon(coupon);
    return orderRepository.save(order);
}
```
연관 관계의 주인이 해당 참조할 객체를 넣어줘야 데이터베이스의 칼럼에 외래 키가 저장됩니다. 즉 Order가 연관 관계의 주인이면 (1)번 멤버 필드에 Coupon을 넣어줘야 데이터베이스 order 테이블에 coupon_id 칼럼에 저장됩니다.

양방향 연관 관계일 경우 위처럼 연관 관계 편의 메소드를 작성하는 것이 좋습니다. 위에서 말했듯이 연관 관계의 주인만이 외래 키를 관리 할 수 있으니 applyCoupon 메소드는 이해하는데 어렵지 않습니다.

그렇다면 use 메서드에서에 데이터베이스에 저장하지도 않는 Order를 set을 왜 해주는 걸까요?

```java
public void use(final Order order) {
//  this.order = order; 해당코드를 주석했을 때 테스트 코드
    this.use = true;
} 
@Test
public void use_메서드에_order_set_필요이유() {
    final Order order = orderService.order();
    assertThat(order.getPrice(), is(9_000D)); // 1,000 할인 적용 확인
    final Coupon coupon = order.getCoupon();
    assertThat(coupon.getOrder(), is(notNullValue())); // 해당 검사는 실패한다.
}
```
order를 바인딩하는 코드를 주석하고 해당 코드를 돌려보면 실패하게 됩니다. 일반적으로 생각했을 때 order 생성 시 1,000할인 쿠폰을 적용했기 때문에 해당 쿠폰에도 주문 객체가 들어갔을 거로 생각할 수 있습니다. 하지만 위의 주석시킨 코드가 그 기능을 담당했기 때문에 쿠폰 객체의 주문 값은 null인 상태입니다. **즉 순수한 객체까지 고려한 양방향 관계를 고려하는 것이 바람직하고 그것이 안전합니다.**

## 제약 조건으로 인한 안정성 및 성능 향상

```java
public class Order {
    ...

    @OneToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "id", nullable = false)
    private Coupon coupon;
}
```

모든 주문에 할인 쿠폰이 적용된다면 @JoinColumn의 nullable 옵션을 false로 주는 것이 좋습니다. **NOT NULL 제약 조건을 준수해서 안전성이 보장됩니다.**

![](https://i.imgur.com/bHfKh8m.png)
* nullable = false 없는 경우, outer join

![](https://i.imgur.com/94To549.png)
* nullable = false 선언한 경우, inner join

**외래 키에 NOT NULL 제약 조건을 설정하면 값이 있는 것을 보장합니다. 따라서 JPA는 이때 내부조인을 
통해서 내부 조인 SQL을 만들어 주고 이것은 외부 조인보다 성능과 최적화에 더 좋습니다.**

물론 모든 경우에 적용할 수는 없고 반드시 외래 키가 NOT NULL인 조건에만 사용할 수 있습니다. 예를 들어 쿠폰과 회원 연관 관계가 있을 때 쿠폰은 반드시 회원의 외래 키를 참조하고 있어야 합니다. 이런 경우 유용하게 사용할 수 있습니다.
