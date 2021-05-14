create table charges
(
    id      bigint auto_increment
        primary key,
    version int,
    item_id bigint       not null,
    name    varchar(100) null,
    charge  bigint       null
);

create table discounts
(
    id        bigint auto_increment
        primary key,
    goods_id  bigint       not null,
    name      varchar(100) null,
    discounts double       null
);

create table goods
(
    id        bigint auto_increment
        primary key,
    master_id bigint       not null,
    name      varchar(100) null
);

create table itemgoods
(
    id       bigint auto_increment
        primary key,
    item_id  bigint not null,
    goods_id bigint not null
);

create table items
(
    id        bigint auto_increment
        primary key,
    version int,
    master_id bigint       not null,
    name      varchar(100) null
);

create table masters
(
    id          bigint auto_increment
        primary key,
    version     int,
    name        varchar(100) not null,
    information varchar(500) null
);

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

create table reservations
(
    id                 bigint auto_increment
        primary key,
    master_id          bigint not null,
    schedule_id        bigint not null,
    member_id          bigint not null,
    item_id            bigint not null,
    goods_id           bigint not null,
    reservation_date   date   not null,
    reservation_charge double not null
);

create table schedules
(
    id         bigint auto_increment
        primary key,
    version int,
    master_id  bigint      not null,
    start_time varchar(20) null,
    end_time   varchar(20) null
);


