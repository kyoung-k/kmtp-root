package com.kmtp.reservation.persistence;

import com.kmtp.common.generic.GenericEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("charges")
@AllArgsConstructor
public class ChargeEntity extends GenericEntity<ChargeEntity> {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("master_id")
    private Long masterId;
    @Column("item_id")
    private Long itemId;
    @Column("charge")
    private Long charge;
    @Column("name")
    private String name;
}