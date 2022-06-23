package com.ybigtaconference.gridtrading.db.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "VOLUME")
    private Double volume;

    @Column(name = "SIDE")
    private String side;

    @Column(name = "IS_DONE")
    private Integer isDone;

//    @Column(name = "CREATED_AT")
//    private LocalDateTime created_at;
//
//    @Column(name = "UUID")
//    private String uuid;

    public Order() {
    }

    public Order(Double price, Double volume, String side) {
        this.price = price;
        this.volume = volume;
        this.side = side;
    }
}
