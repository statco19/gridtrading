package com.ybigtaconference.gridtrading.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ybigtaconference.gridtrading.producer.KafkaProducer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
@Getter @Setter
public class BotServiceImpl implements BotService {

    public String accessKey,secretKey;
    private static final String serverUrl = "https://api.upbit.com";

//    private UpbitImpl upbit;
//    private UtilService utilService;
////    private KafkaProducer producer;
//
//    @Autowired
//    public KafkaTemplate<String,String> customKafkaTemplate = new KafkaTemplate<String,String>(
//
//    );

    private final UpbitImpl upbit;
    private final UtilService utilService;
    private final KafkaProducer producer;

//    private OrderService orderService;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

//    public BotServiceImpl() {
//
//    }
//
//    public BotServiceImpl(String accessKey, String secretKey) {
//        this.accessKey = accessKey;
//        this.secretKey = secretKey;
//        upbit = new UpbitImpl(this.accessKey, this.secretKey);
//        utilService = new UtilServiceImpl();
////        producer = new KafkaProducer(customKafkaTemplate);
//    }

    @Override
    public void connect_upbit() {
        try {
            upbit.setAccessKey(this.accessKey);
            upbit.setSecretKey(this.secretKey);

            String balance = upbit.get_balances();
            log.info("연결 성공");
            log.info("balance {}", balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancel_all_order(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        upbit.setAccessKey(this.accessKey);
        upbit.setSecretKey(this.secretKey);

        String orderList = upbit.get_order("KRW-" + coin);
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
        upbit.setAccessKey(this.accessKey);
        upbit.setSecretKey(this.secretKey);

        try {
            String volume = upbit.get_balance(coin);
            String stoploss = upbit.order("KRW-" + coin, null, Double.parseDouble(volume), "ask", "market");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Double get_avg(String coin) {
        upbit.setAccessKey(this.accessKey);
        upbit.setSecretKey(this.secretKey);

        try {
            String balances = upbit.get_balances();
            JsonArray jsonArray = gson.fromJson(balances, JsonArray.class).getAsJsonArray();

            for(JsonElement jsonElement : jsonArray) {
                if (jsonElement.getAsJsonObject().get("currency").getAsString().equals(coin)) {
                    Double avg_price = Double.parseDouble(jsonElement.getAsJsonObject().get("avg_buy_price").getAsString());
                    return avg_price;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, List<Double>> get_open_price(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Map<String, List<Double>> map = new HashMap<>();
        List<Double> bid_list = new ArrayList<>();
        List<Double> ask_list = new ArrayList<>();

        upbit.setAccessKey(this.accessKey);
        upbit.setSecretKey(this.secretKey);

        try {
            String orders = upbit.get_order("KRW-" + coin);
            JsonArray jsonArray = gson.fromJson(orders, JsonArray.class).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                if (jsonElement.getAsJsonObject().get("side").getAsString().equals("bid")) {
                    bid_list.add(Double.parseDouble(jsonElement.getAsJsonObject().get("price").getAsString()));
                } else {
                    ask_list.add(Double.parseDouble(jsonElement.getAsJsonObject().get("price").getAsString()));
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
    public void levels_order(String coin, Double volume, List<Double> levels) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        upbit.setAccessKey(this.accessKey);
        upbit.setSecretKey(this.secretKey);

        try {
            for (Double price : levels) {
                upbit.order("KRW-" + coin, price, volume, "bid", "limit");
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Envr set_env(Params params) throws Exception {
        upbit.setAccessKey(this.accessKey);
        upbit.setSecretKey(this.secretKey);
        Envr envr = new Envr(params);

        String current = upbit.get_current_price("KRW-" + envr.getCoin());
        String balance_KRW = upbit.get_balance("KRW");

        if(Double.parseDouble(balance_KRW) < envr.getBudget()) {
            throw new Exception("현금 보유량이 예산보다 적습니다.");
        }
        String a = "27000.000";

        System.out.println("here "+Double.parseDouble(a));
        System.out.println("here "+current);
        System.out.println("here "+Double.valueOf(current));
//        BigDecimal bigDecimal = new BigDecimal(String.format("%.8f", Double.parseDouble("2345678.7654321")));

//        log.info("Double ? {}", bigDecimal);
        Double dou = 1000.000;

        System.out.println(utilService.get_price_scale_tick(dou));

        Double scale = utilService.get_price_scale_tick(Double.parseDouble(current));
//        Double scale = utilService.get_price_scale_tick(bigDecimal);
        envr.setScale(scale);

        envr.setUpper(Double.parseDouble(current));
        // envr.setLower is done

        List<Double> levels = utilService.make_levels(envr.getMode(), envr.getUpper(), envr.getLower(), envr.getGrids());
        Double diff = levels.get(levels.size() - 1);
        Double diff_removed = levels.remove(levels.size() - 1);
//        log.info("remove diff {}", diff_removed);

        envr.setLevels(levels);
        envr.setDiff(diff);

        Double volume = utilService.get_volume(Double.parseDouble(current), envr.getBudget(), envr.getGrids());
        envr.setVolume(volume);

        log.info("{}", LocalDateTime.now());
        log.info("\n");
        log.info("{} 현재가(KRW): {}", envr.getCoin(), current);
        log.info("호가단위(KRW): {}", scale);
        log.info("Levels: {}", levels);
        log.info("Volume({})", volume);

        if(envr.getMode().equals("Arithmetic")) {
            log.info("Difference(KRW): {}", String.format("%.4f", diff));
            log.info("수익률: {}", String.format("%.2f", diff/(Double.parseDouble(current))*100));
        } else {
            log.info("Difference(%): {}", String.format("%.2f", (diff-1)*100));
            log.info("수익률: {}", String.format("%.2f", (diff-1)*100));
        }

        return envr;

    }

    @Override
    public void trade(Envr env) throws Exception {
        upbit.setAccessKey(this.accessKey);
        upbit.setSecretKey(this.secretKey);

        List<String> before_list = new ArrayList<>();
        List<String> now_list = new ArrayList<>();

        // 전체 주문 취소
        this.cancel_all_order(env.getCoin());
        // 초기 매수 주문
        this.levels_order(env.getCoin(), env.getVolume(), env.getLevels());
        String firstOrders = upbit.get_order("KRW-" + env.getCoin());
        JsonArray first_open = gson.fromJson(firstOrders, JsonArray.class).getAsJsonArray();


        log.info("{}", this.get_open_price(env.getCoin()));
        for(JsonElement jsonElement : first_open) {
            before_list.add(jsonElement.toString());
            producer.send(jsonElement.toString());
        }

        while(true) {
            Thread.sleep(500);

            Double current = Double.parseDouble(upbit.get_current_price("KRW-" + env.getCoin()));
            Double avg_price = this.get_avg(env.getCoin());

            if(avg_price != null) {
                Double profit = (current-avg_price)/avg_price*100;
                if(profit < (-1)*env.getStop_loss()) {
                    log.info("**STOP LOSS 실행**");
                    log.info("{} 현재가(KRW): {}", env.getCoin(), current);
                    log.info("{} 평단가(KRW): {}", env.getCoin(), avg_price);
                    log.info("현재 손익: {}", String.format("%.2f", profit));

                    // 모든 주문 취소
                    this.cancel_all_order(env.getCoin());

                    // 모든 코인 시장가 매도
                    this.sell_all_market(env.getCoin());

                    log.info("종료");
                    break;
                }
            }

            // 미체결 주문수가 GRIDS와 다르면 추가 주문
            String orders = upbit.get_order("KRW-" + env.getCoin());
            JsonArray now_open = gson.fromJson(orders, JsonArray.class).getAsJsonArray();

            if(!Objects.equals(env.getGrids(), now_open.size())) {
                now_list = new ArrayList<>();
                for(JsonElement jsonElement : now_open) {
                    now_list.add(jsonElement.toString());
                }

                String doneOrder_uuid = "";
                for(String order : before_list) {
                    if(!now_list.contains(order)) {
                        doneOrder_uuid = gson.fromJson(order,JsonElement.class)
                                .getAsJsonObject()
                                .get("uuid")
                                .getAsString();

                        String doneOrder = upbit.get_order_uuid(doneOrder_uuid);

                        // producer
                        producer.send(doneOrder);
                        log.info("doneOrder {} ", doneOrder);
//                        break;
                    }
                }

                Double balance_coin = 0.;

                try {
                    balance_coin = Double.parseDouble(upbit.get_balance(env.getCoin()));
                } catch (Exception e) {
                    e.printStackTrace();
                    balance_coin = 0.;
                }
                Double last_price = Double.parseDouble(upbit.get_current_price("KRW-" + env.getCoin()));

                String ret = "";
                String side;
                Double price = null;
                if(balance_coin != 0) {
                    side = "bid";
                    if(env.getMode().equals("Arithmetic")) {
                        price = last_price + env.getDiff();
                    } else {
                        price = last_price * env.getDiff();
                    }
                    price = utilService.get_tick_size(price, "floor");
                    ret = upbit.order("KRW-" + env.getCoin(), price, env.getVolume(), "ask", "limit");
                } else {
                    side = "ask";
                    if(env.getMode().equals("Arithmetic")) {
                        price = last_price - env.getDiff();
                    } else {
                        price = last_price / env.getDiff();
                    }
                    price = utilService.get_tick_size(price, "floor");
                    ret = upbit.order("KRW-" + env.getCoin(), price, env.getVolume(), "bid", "limit");

                }

                if(ret.contains("error")) {
                    log.info("trade error");
                }

                else {
                    log.info("");
                    log.info("{}", LocalDateTime.now());
                    log.info("{} -> {}KRW {}{} 체결", side, last_price, env.getVolume(), env.getCoin());
                    log.info("{}", this.get_open_price(env.getCoin()));
                }
            } else {
                before_list = new ArrayList<>();
                for(JsonElement jsonElement : now_open) {
                    before_list.add(jsonElement.toString());
                }
            }

        }
    }
}
