select (select count(*) from orders), (select count(*) from reservations), (select count(*) from transactions);
select (select count(*) from orders where status != 'COMPLETE') not_complete,
  (select count(*) from reservations where status != 'CONFIRMED') not_confirmed,
  (select count(*) from transactions where status != 'AUTHORIZED') not_authorized;

delete from orders;
delete from reservations;
delete from transactions;

select o.*, r.*, t.* from orders o left outer join reservations r on o.id = r.order_id left outer join transactions t on r.order_id = t.order_id where o.status = 'NEW';
select r.*, t.* from reservations r left outer join transactions t on r.order_id = t.order_id where r.status = 'PENDING';
