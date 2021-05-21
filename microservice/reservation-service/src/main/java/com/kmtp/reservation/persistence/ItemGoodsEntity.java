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
@Table("itemgoods")
@AllArgsConstructor
public class ItemGoodsEntity {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("item_id")
    private Long itemId;
    @Column("goods_id")
    private Long goodsId;
}