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
@Table("items")
@AllArgsConstructor
public class ItemEntity extends GenericEntity<ItemEntity> {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("master_id")
    private Long masterId;
    @Column("name")
    private String name;
}