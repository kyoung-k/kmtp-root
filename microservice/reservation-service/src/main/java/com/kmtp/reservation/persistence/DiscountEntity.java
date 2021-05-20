package com.kmtp.reservation.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("discounts")
@AllArgsConstructor
public class DiscountEntity {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("master_id")
    private Long masterId;
    @Column("goods_id")
    private Long goodsId;
    @Column("name")
    private String name;
    @Column("discount")
    private Double discount;
}
