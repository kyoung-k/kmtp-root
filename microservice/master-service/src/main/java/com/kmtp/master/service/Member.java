package com.kmtp.master.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Member {

    private Long id;
    private String email;
    private String name;
    private int age;
    private String address;
}
