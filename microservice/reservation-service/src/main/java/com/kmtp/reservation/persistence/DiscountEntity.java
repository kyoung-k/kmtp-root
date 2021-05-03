package com.kmtp.reservation.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("discounts")
public class DiscountEntity {

    @Id
    private Long id;
    @Column("goods_id")
    private Long goodsId;
    @Column("name")
    private String name;
    @Column("discount")
    private Double discount;
}
