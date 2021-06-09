-- 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


drop table charges;
create table charges
(
    id        bigint auto_increment
        primary key,
    version   int          null,
    master_id bigint       not null,
    item_id   bigint       not null,
    name      varchar(100) null,
    charge    bigint       null
);

INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (1, 0, 1, 1, '101호 가격', 10000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (2, 0, 1, 2, '102호 가격', 10000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (3, 0, 1, 3, '103호 가격', 10000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (4, 0, 1, 4, '201호 가격', 12000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (5, 0, 1, 5, '202호 가격', 12000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (6, 0, 1, 6, '301호 가격', 15000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (7, 0, 1, 7, '302호 가격', 15000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (8, 0, 2, 8, '사진실 1호', 40000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (9, 0, 2, 9, '사진실 2호', 40000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (10, 0, 2, 10, '사진실 3호', 60000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (11, 0, 3, 11, '남자 커트 가격', 18000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (12, 0, 3, 12, '여자 커트 가격', 20000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (13, 0, 3, 13, '펌 가격', 70000);
INSERT INTO kmtp.charges (id, version, master_id, item_id, name, charge) VALUES (14, 0, 3, 14, '염색 가격', 30000);

drop table discounts;
create table discounts
(
    id        bigint auto_increment
        primary key,
    version   int          null,
    master_id bigint       not null,
    goods_id  bigint       not null,
    name      varchar(100) null,
    discount  double       null
);

INSERT INTO kmtp.discounts (id, version, master_id, goods_id, name, discount) VALUES (1, 0, 1, 1, '여름맞이 상품 할인율', 0.15);
INSERT INTO kmtp.discounts (id, version, master_id, goods_id, name, discount) VALUES (2, 0, 1, 2, '3층 전용 상품 할인율', 0.1);
INSERT INTO kmtp.discounts (id, version, master_id, goods_id, name, discount) VALUES (3, 0, 3, 3, '김 실장', 0);
INSERT INTO kmtp.discounts (id, version, master_id, goods_id, name, discount) VALUES (4, 0, 3, 4, '민 디자이너', 0);

drop table goods;
create table goods
(
    id        bigint auto_increment
        primary key,
    version   int          null,
    master_id bigint       not null,
    name      varchar(100) null
);

INSERT INTO kmtp.goods (id, version, master_id, name) VALUES (1, 0, 1, '여름맞이 상품');
INSERT INTO kmtp.goods (id, version, master_id, name) VALUES (2, 0, 1, '3층 전용 상품');
INSERT INTO kmtp.goods (id, version, master_id, name) VALUES (3, 0, 3, '김 실장');
INSERT INTO kmtp.goods (id, version, master_id, name) VALUES (4, 0, 3, '민 디자이너');

drop table itemgoods;
create table itemgoods
(
    id       bigint auto_increment
        primary key,
    version  int    null,
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
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (10, 0, 11, 3);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (11, 0, 12, 3);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (12, 0, 13, 3);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (13, 0, 14, 3);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (14, 0, 11, 4);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (15, 0, 12, 4);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (16, 0, 13, 4);
INSERT INTO kmtp.itemgoods (id, version, item_id, goods_id) VALUES (17, 0, 14, 4);

drop table items;
create table items
(
    id        bigint auto_increment
        primary key,
    version   int          null,
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
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (8, 0, 2, '사진실 1호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (9, 0, 2, '사진실 2호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (10, 0, 2, '사진실 3호');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (11, 0, 3, '남자 커트');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (12, 0, 3, '여자 커트');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (13, 0, 3, '펌');
INSERT INTO kmtp.items (id, version, master_id, name) VALUES (14, 0, 3, '염색');

drop table masters;
create table masters
(
    id          bigint auto_increment
        primary key,
    version     int          null,
    master_type varchar(100) not null,
    name        varchar(100) not null,
    information varchar(500) null
);

INSERT INTO kmtp.masters (id, version, master_type, name, information) VALUES (1, 0, 'CONSECUTIVE', 'KYoung Hotel', '기영이 호텔');
INSERT INTO kmtp.masters (id, version, master_type, name, information) VALUES (2, 0, 'NONCONSECUTIVE', 'KYoung Studio', '기영이 사진관');
INSERT INTO kmtp.masters (id, version, master_type, name, information) VALUES (3, 0, 'NONCONSECUTIVE', 'KYoung Salon', '기영이 살롱');

drop table members;
create table members
(
    id      bigint auto_increment
        primary key,
    version int              null,
    email   varchar(100)     null,
    name    varchar(100)     null,
    age     tinyint unsigned null,
    address varchar(100)     null
);

INSERT INTO kmtp.members (id, version, email, name, age, address) VALUES (1, 0, 'kyoung.k.0613@gmail.com', '김기영', 37, 'i-devlab.com');
INSERT INTO kmtp.members (id, version, email, name, age, address) VALUES (2, 1, 'raone.k.0130@gmail.com', '김라원', 3, '알콩달콩 어린이집');

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
    version    int         null,
    master_id  bigint      not null,
    start_time varchar(20) null,
    end_time   varchar(20) null
);

INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (1, 0, 1, '00:00', '24:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (2, 0, 2, '10:00', '12:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (3, 0, 2, '14:00', '16:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (4, 0, 2, '16:00', '18:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (5, 0, 2, '18:00', '20:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (6, 0, 3, '10:00', '11:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (7, 0, 3, '11:00', '12:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (8, 0, 3, '12:00', '13:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (9, 0, 3, '13:00', '14:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (10, 0, 3, '14:00', '15:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (11, 0, 3, '15:00', '16:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (12, 0, 3, '16:00', '17:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (13, 0, 3, '17:00', '18:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (14, 0, 3, '18:00', '19:00');
INSERT INTO kmtp.schedules (id, version, master_id, start_time, end_time) VALUES (15, 0, 3, '19:00', '20:00');
