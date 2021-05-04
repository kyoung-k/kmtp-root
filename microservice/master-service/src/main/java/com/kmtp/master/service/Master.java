package com.kmtp.master.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Master {

    private Long id;
    private String name;
    private String information;
}
