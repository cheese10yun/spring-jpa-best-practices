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

insert into account
  (address1, address2, zip, created_at, update_at, email, first_name, last_name, password_expiration_date, password_failed_count, password_ttl, password, id)
values
  ('address1', 'address2', '002', '2019-01-20 00:00:01', '2019-01-20 00:00:00', 'test001@test.com', 'first', 'last', '20120-01-20 00:00:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 1),
  ('address1', 'address2', '002', '2019-01-20 00:00:02', '2019-01-20 00:00:00', 'test002@test.com', 'first', 'last', '20120-01-20 00:01:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 2),
  ('address1', 'address2', '002', '2019-01-20 00:00:03', '2019-01-20 00:00:00', 'test003@test.com', 'first', 'last', '20120-01-20 00:02:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 3),
  ('address1', 'address2', '002', '2019-01-20 00:00:04', '2019-01-20 00:00:00', 'test004@test.com', 'first', 'last', '20120-01-20 00:03:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 4),
  ('address1', 'address2', '002', '2019-01-20 00:00:05', '2019-01-20 00:00:00', 'test005@test.com', 'first', 'last', '20120-01-20 00:04:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 5),
  ('address1', 'address2', '002', '2019-01-20 00:00:06', '2019-01-20 00:00:00', 'test006@test.com', 'first', 'last', '20120-01-20 00:05:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 6),
  ('address1', 'address2', '002', '2019-01-20 00:00:07', '2019-01-20 00:00:00', 'test007@test.com', 'first', 'last', '20120-01-20 00:06:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 7),
  ('address1', 'address2', '002', '2019-01-20 00:00:08', '2019-01-20 00:00:00', 'test008@test.com', 'first', 'last', '20120-01-20 00:07:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 8),
  ('address1', 'address2', '002', '2019-01-20 00:00:09', '2019-01-20 00:00:00', 'test009@test.com', 'first', 'last', '20120-01-20 00:08:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 9),
  ('address1', 'address2', '002', '2019-01-20 00:00:10', '2019-01-20 00:00:00', 'test010@test.com', 'first', 'last', '20120-01-20 00:09:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 10),
  ('address1', 'address2', '002', '2019-01-20 00:00:11', '2019-01-20 00:00:00', 'test011@test.com', 'first', 'last', '20120-01-20 00:10:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 11),
  ('address1', 'address2', '002', '2019-01-20 00:00:12', '2019-01-20 00:00:00', 'test012@test.com', 'first', 'last', '20120-01-20 00:11:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 12),
  ('address1', 'address2', '002', '2019-01-20 00:00:13', '2019-01-20 00:00:00', 'test013@test.com', 'first', 'last', '20120-01-20 00:12:00', 0, 1209604, '$2a$10$tI3Y.nhgC.73LYCszoCaLu3nNEIM4QgeACiNseWlvr1zjrV5NCCs6', 13);