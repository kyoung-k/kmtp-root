package com.kmtp.reservation.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("charges")
public class ChargeEntity {

    @Id
    private Long id;
    @Column("item_id")
    private Long itemId;
    @Column("charge")
    private Long charge;
}