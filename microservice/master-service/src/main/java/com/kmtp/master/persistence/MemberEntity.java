package com.kmtp.master.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("members")
public class MemberEntity {

    @Id
    private Long id;
    @Column("email")
    private String email;
    @Column("name")
    private String name;
    @Column("age")
    private int age;
    @Column("address")
    private String address;
}
