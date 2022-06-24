package com.ybigtaconference.gridtrading.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Getter @Setter
public class BotServiceImpl implements BotService {

    private String accessKey,secretKey;
    private static final String serverUrl = "https://api.upbit.com";

    private final UtilService utilService = new UtilServiceImpl();
//    private final OrderService orderService = new OrderServiceImpl();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Upbit upbit;
    public BotServiceImpl(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        upbit = new UpbitImpl(this.accessKey, this.secretKey);
    }


    @Override
    public void connect_upbit() {
        try {
            String balance = upbit.get_balances();
            log.info("연결 성공");
            log.info("balance {}", balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel_all_order(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String orderList = upbit.get_order(coin);
        JsonArray jsonArray = gson.fromJson(orderList, JsonArray.class).getAsJsonArray();

        for(JsonElement jsonElement : jsonArray) {
            try {
                String uuid = jsonElement.getAsJsonObject().get("uuid").getAsString();
                upbit.cancel_order(uuid);
//                orderService
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sell_all_market(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            String volume = upbit.get_balance(coin);
            String stoploss = upbit.order("KRW-" + coin, null, Float.parseFloat(volume), "ask", "market");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Float get_avg(String coin) {
        try {
            String balances = upbit.get_balances();
            JsonArray jsonArray = gson.fromJson(balances, JsonArray.class).getAsJsonArray();

            for(JsonElement jsonElement : jsonArray) {
                if (jsonElement.getAsJsonObject().get("currency").getAsString().equals(coin)) {
                    Float avg_price = Float.parseFloat(jsonElement.getAsJsonObject().get("avg_buy_price").getAsString());
                    return avg_price;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, List<Float>> get_open_price(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, List<Float>> map = new HashMap<>();
        List<Float> bid_list = new ArrayList<>();
        List<Float> ask_list = new ArrayList<>();

        try {
            String orders = upbit.get_order("KRW-" + coin);
            JsonArray jsonArray = gson.fromJson(orders, JsonArray.class).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                if (jsonElement.getAsJsonObject().get("side").getAsString().equals("bid")) {
                    bid_list.add(Float.parseFloat(jsonElement.getAsJsonObject().get("price").getAsString()));
                } else {
                    ask_list.add(Float.parseFloat(jsonElement.getAsJsonObject().get("price").getAsString()));
                }
            }

            Collections.sort(bid_list);
            Collections.sort(ask_list);
            map.put("bid", bid_list);
            map.put("ask", ask_list);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void levels_order(String coin, Float volume, List<Float> levels) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            for (Float price : levels) {
                upbit.order("KRW-" + coin, price, volume, "bid", "limit");
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Envr set_env(Params params) throws Exception {
        Envr envr = new Envr(params);

        String current = upbit.get_current_price("KRW-" + envr.getCoin());
        String balance_KRW = upbit.get_balance("KRW");

        if(Float.parseFloat(balance_KRW) < envr.getBudget()) {
            throw new Exception("현금 보유량이 예산보다 적습니다.");
        }

        Float scale = utilService.get_price_scale_tick(Float.parseFloat(current));
        envr.setScale(scale);

        envr.setUpper(Float.parseFloat(current));
        // envr.setLower is done

        List<Float> levels = utilService.make_levels(envr.getMode(), envr.getUpper(), envr.getLower(), envr.getGrids());
        Float diff = levels.get(levels.size() - 1);
        envr.setLevels(levels);
        envr.setDiff(diff);

        Float volume = utilService.get_volume(Float.parseFloat(current), envr.getBudget(), envr.getGrids());
        envr.setVolume(volume);

        log.info("{}", LocalDateTime.now());
        log.info("\n");
        log.info("{} 현재가(KRW): {}", envr.getCoin(), current);
        log.info("호가단위(KRW): {}", scale);
        log.info("Levels: {}", levels);
        log.info("Volume({})", volume);

        if(envr.getMode().equals("Arithmetic")) {
            log.info("Difference(KRW): {}", String.format("%.4f", diff));
            log.info("수익률: {}", String.format("%.2f", diff/(Float.parseFloat(current))*100));
        } else {
            log.info("Difference(%): {}", String.format("%.2f", (diff-1)*100));
            log.info("수익률: {}", String.format("%.2f", (diff-1)*100));
        }

        return envr;

    }

    @Override
    public void trade(Envr env) {

    }
}
