package com.ybigtaconference.gridtrading.service;

import com.ybigtaconference.gridtrading.credentials.TokenAndKey;
import com.ybigtaconference.gridtrading.db.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BotServiceImplTest {

    @Autowired
    private UpbitImpl upbit;

    @Autowired
    private UtilService utilService;

    @Autowired
    private OrderService orderService;

//    @Autowired
//    private OrderRepository orderRepository;

    @Autowired
    private BotServiceImpl botService;

//    @BeforeEach
//    public void beforeEach() {
//    }

    @Test
    void connect_upbit() {
        botService.setAccessKey(TokenAndKey.ACCESS_KEY);
        botService.setSecretKey(TokenAndKey.SECRET_KEY);

        botService.connect_upbit();
    }

    @Test
    void cancel_all_order() {
    }

    @Test
    void sell_all_market() {
    }

    @Test
    void get_avg() {
    }

    @Test
    void get_open_price() {
    }

    @Test
    void levels_order() {
    }

    @Test
    void set_env() {
    }

    @Test
    void trade() {
    }
}