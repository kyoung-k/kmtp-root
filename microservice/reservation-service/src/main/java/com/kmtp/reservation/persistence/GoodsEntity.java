package com.kmtp.reservation.persistence;

import com.kmtp.common.generic.GenericEntity;
import com.kmtp.reservation.endpoint.Goods;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("goods")
@AllArgsConstructor
public class GoodsEntity extends GenericEntity<GoodsEntity> {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("master_id")
    private Long masterId;
    @Column("name")
    private String name;
}
