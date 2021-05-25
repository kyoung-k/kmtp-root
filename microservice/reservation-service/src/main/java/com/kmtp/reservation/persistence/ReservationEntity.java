package com.kmtp.reservation.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Builder
@Table("reservations")
@AllArgsConstructor
public class ReservationEntity {

    @Id
    private Long id;
    @Version
    private Long version;
    @Column("master_id")
    private Long masterId;
    @Column("schedule_id")
    private Long scheduleId;
    @Column("member_id")
    private Long memberId;
    @Column("item_id")
    private Long itemId;
    @Column("goods_id")
    private Long goodsId;
    @Column("reservation_date")
    private LocalDate reservationDate;
    @Column("reservation_charge")
    private Integer reservationCharge;
}
