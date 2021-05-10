package com.kmtp.master.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Schedule {

    private Long id;
    private Long masterId;
    private String startTime;
    private String endTime;
}
