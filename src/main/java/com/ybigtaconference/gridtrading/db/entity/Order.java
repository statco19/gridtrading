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

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "ORDER_PRICE")
    private String order_price;

    @Column(name = "TRADE_PRICE")
    private String trade_price;  // -1:cancel, 0:wait >0: done

    @Column(name = "VOLUME")
    private String volume;

    @Column(name = "SIDE")
    private String side;

//    @Column(name = "CREATED_AT")
//    private LocalDateTime created_at;


    public Order() {
    }

    public Order(String uuid, String order_price, String trade_price, String volume, String side) {
        this.uuid = uuid;
        this.order_price = order_price;
        this.trade_price = trade_price;
        this.volume = volume;
        this.side = side;
    }
}
