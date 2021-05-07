package com.kmtp.master.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Master {

    private Long id;
    private String name;
    private String information;
}
