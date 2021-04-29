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
