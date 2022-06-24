package com.ybigtaconference.gridtrading.service.test;


import com.ybigtaconference.gridtrading.credentials.TokenAndKey;
import com.ybigtaconference.gridtrading.db.repository.OrderRepository;
import com.ybigtaconference.gridtrading.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UtilServiceTest {

//    static BotServiceImpl botService = new BotServiceImpl(TokenAndKey.ACCESS_KEY, TokenAndKey.SECRET_KEY);
    static UpbitImpl upbit = new UpbitImpl(TokenAndKey.ACCESS_KEY, TokenAndKey.SECRET_KEY);

    public static void main(String[] args) throws Exception {
//        params.mode = this.mode;
//        params.coin = this.coin;
//        params.grids = this.grids;
//        params.budget = this.budget;
//        params.lower = this.lower;
//        params.stop_loss = this.stop_loss;
        Params params = new Params();
        params.setMode("Arithmetic");
        params.setCoin("BTC");
        params.setGrids(100);
        params.setBudget(20000.);
        params.setLower(25000000.);
        params.setStop_loss(10.);

//        Envr env = botService.set_env(params);

    }
}
