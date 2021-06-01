-- 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
select * from masters;
select * from schedules;
select * from items;
select * from charges;
select * from goods;
select * from discounts;
select * from itemgoods;
select * from reservations;
select * from members;


drop table charges;
create table charges
(
    id      bigint auto_increment
        primary key,
    version int,
    master_id bigint     not null,
    item_id bigint       not null,
    name    varchar(100) null,
    charge  bigint       null
);

INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (1, 0, 1, 1, '101호 가격', 10000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (2, 0, 1, 2, '102호 가격', 10000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (3, 0, 1, 3, '103호 가격', 10000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (4, 0, 1, 4, '201호 가격', 12000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (5, 0, 1, 5, '202호 가격', 12000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (6, 0, 1, 6, '301호 가격', 15000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (7, 0, 1, 7, '302호 가격', 15000);

drop table discounts;
create table discounts
(
    id        bigint auto_increment
        primary key,
    version int,
    master_id bigint       not null,
    goods_id  bigint       not null,
    name      varchar(100) null,
    discount double  null
);

INSERT INTO kmtp.discounts (id, version, master_id, goods_id, name, discount) VALUES (1, 0, 1, 1, '여름맞이 상품 할인율', 0.15);
INSERT INTO kmtp.discounts (id, version, master_id, goods_id, name, discount) VALUES (2, 0, 1, 2, '3층 전용 상품 할인율', 0.1);

drop table goods;
create table goods
(
    id        bigint auto_increment
        primary key,
    version int,
    master_id bigint       not null,
    name      varchar(100) null
);

INSERT INTO kmtp.goods (id, version, master_id, name) VALUES (1, 0, 1, '여름맞이 상품');
INSERT INTO kmtp.goods (id, version, master_id, name) VALUES (2, 0, 1, '3층 전용 상품');

drop table itemgoods;
create table itemgoods
(
    id       bigint auto_increment
        primary key,
    version int,
    item_id  bigint not null,
    goods_id bigint not null
);

INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (1, 0, 1, 1);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (2, 0, 5, 1);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (3, 0, 6, 1);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (4, 0, 7, 1);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (5, 0, 4, 1);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (6, 0, 2, 1);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (7, 0, 3, 1);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (8, 0, 6, 2);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (9, 0, 7, 2);

drop table items;
create table items
(
    id        bigint auto_increment
        primary key,
    version int,
    master_id bigint       not null,
    name      varchar(100) null
);

INSERT INTO kmtp.items (id, version, master_id, name) VALUES (1, 0, 1, '101호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (2, 0, 1, '102호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (3, 0, 1, '103호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (4, 0, 1, '201호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (5, 0, 1, '202호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (6, 0, 1, '301호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (7, 0, 1, '302호');

drop table masters;
create table masters
(
    id          bigint auto_increment
        primary key,
    version     int,
    master_type varchar(100) not null,
    name        varchar(100) not null,
    information varchar(500) null
);

INSERT INTO kmtp.masters (id, version, master_type, name, information) VALUES (1, 0, 'CONSECUTIVE', 'KYoung Hotel', '기영이 호텔');
INSERT INTO kmtp.masters (id, version, master_type, name, information) VALUES (2, 0, 'NONCONSECUTIVE', 'KYoung Hair', '기영이 미용실');

drop table members;
create table members
(
    id      bigint auto_increment
        primary key,
    version int,
    email   varchar(100)     null,
    name    varchar(100)     null,
    age     tinyint unsigned null,
    address varchar(100)     null
);

INSERT INTO kmtp.members (id, version, email, name, age, address) VALUES (1, 0, 'kyoung.k.0613@gmail.com', '김기영', 37, 'i-devlab.com');

drop table reservations;
create table reservations
(
    id                 bigint auto_increment
        primary key,
    version int,
    master_id          bigint not null,
    schedule_id        bigint not null,
    member_id          bigint null,
    item_id            bigint not null,
    goods_id           bigint null,
    start_date   date   not null,
    end_date   date   not null,
    reservation_charge double not null
);

drop table schedules;
create table schedules
(
    id         bigint auto_increment
        primary key,
    version int,
    master_id  bigint      not null,
    start_time varchar(20) null,
    end_time   varchar(20) null
);

INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (1, 0, 1, '00:00', '24:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (2, 0, 2, '10:00', '12:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (3, 0, 2, '14:00', '16:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (4, 0, 2, '16:00', '18:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (5, 0, 2, '18:00', '20:00');