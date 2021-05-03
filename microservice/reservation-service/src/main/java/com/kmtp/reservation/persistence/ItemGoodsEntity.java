package com.kmtp.reservation.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("itemgoods")
public class ItemGoodsEntity {

    @Column("item_id")
    private Long itemId;
    @Column("goods_id")
    private Long goodsId;
}