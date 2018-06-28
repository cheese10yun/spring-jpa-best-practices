insert into coupon
(id, discount_amount, use)
values
  (1, 1000, false),
  (2, 1000, false),
  (3, 1000, false),
  (4, 1000, false);

insert into orders
(id, price, coupon_id)
values
  (2, 20000, 2),
  (3, 30000, 3),
  (4, 40000, 4);