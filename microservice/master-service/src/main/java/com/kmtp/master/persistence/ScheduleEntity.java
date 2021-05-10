package com.kmtp.master.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@Table("schedules")
public class ScheduleEntity {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("master_id")
    private Long masterId;
    @Column("start_time")
    private String startTime;
    @Column("end_time")
    private String endTime;
}
