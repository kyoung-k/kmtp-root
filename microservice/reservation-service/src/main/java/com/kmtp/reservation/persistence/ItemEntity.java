package com.kmtp.reservation.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("items")
public class ItemEntity {

    @Id
    private Long id;
    @Column("master_id")
    private Long masterId;
    @Column("name")
    private String name;
}