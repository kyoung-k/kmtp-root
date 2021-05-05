package com.kmtp.master.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Builder
@AllArgsConstructor
@Table("masters")
public class MasterEntity {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("name")
    private String name;
    @Column("information")
    private String information;
}