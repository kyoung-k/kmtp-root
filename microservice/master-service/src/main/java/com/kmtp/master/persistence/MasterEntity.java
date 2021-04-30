package com.kmtp.master.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Builder
@Table("masters")
public class MasterEntity {

    @Id
    private Long id;
    @Column("name")
    private String name;
    @Column("information")
    private String information;
}

@Data
@Builder
@Table("members")
class MemberEntity {

    @Id
    private Long id;

    private String email;
    private String name;
    private int age;
    private String addres;
}

@Data
@Builder
@Table("items")
class ItemEntity {

    @Id
    private Long id;
    private Long masterId;
    private String name;
}

@Data
@Builder
@Table("charges")
class ChargeEntity {

    @Id
    private Long id;
    private Long itemId;
    private Long charge;
}

@Data
@Builder
@Table("goods")
class GoodsEntity {

    @Id
    private Long id;
    private Long masterId;
    private String name;
}

@Data
@Builder
@Table("discounts")
class DiscountEntity {

    @Id
    private Long id;
    private Long masterId;
    private String name;
    private Long discount;
}

@Data
@Builder
@Table("itemgoods")
class ItemGoodsEntity {

    private Long itemId;
    private Long goodsId;
}