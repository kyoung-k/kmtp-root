package com.kmtp.master.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;


@Data
@Builder
public class MasterEntity {

    @Id
    private Long id;

    private String name;

    private String information;
}
