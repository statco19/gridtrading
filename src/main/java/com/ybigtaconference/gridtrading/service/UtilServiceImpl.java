package com.ybigtaconference.gridtrading.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import lombok.RequiredArgsConstructor;
import okhttp3.ResponseBody;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
@Service
@RequiredArgsConstructor
public class UtilServiceImpl implements UtilService{
    @Override
    public Double get_price_scale_tick(Double price) {

        Double res = 0.010;

        if (price >= 2000000){
            res = 1000.0;
        }else if(price >= 1000000){
            res = 500.0;
        }else if(price >= 500000){
            res = 100.0;
        }else if(price >= 100000){
            res = 50.0;
        }else if(price >= 10000){
            res = 10.0;
        }else if(price >= 1000){
            res = 5.0;
        }else if(price >= 100){
            res = 1.0;
        }else if(price >= 10){
            res = 0.1;
        }else if(price >= 0){
            res = 0.01;
        }

        return res;
    }

    @Override
    public Double get_tick_size(Double price, String method) {
        /* 원화마켓 주문 가격 단위

        Args:
            price (Double]): 주문 가격
            method (str, optional): 주문 가격 계산 방식. Defaults to "floor".

        Returns:
            Double: 업비트 원화 마켓 주문 가격 단위로 조정된 가격
        */

        // tick_size
        Double tick_size = 0.0;
        Double operand = 0.010;

        if (price >= 2000000){
            operand = 1000.0;
        }else if(price >= 1000000){
            operand = 500.0;
        }else if(price >= 500000){
            operand = 100.0;
        }else if(price >= 100000){
            operand = 50.0;
        }else if(price >= 10000){
            operand = 10.0;
        }else if(price >= 1000){
            operand = 5.0;
        }else if(price >= 100){
            operand = 1.0;
        }else if(price >= 10){
            operand = 0.1;
        }else if(price >= 0){
            operand = 0.01;
        }


        tick_size = calculation_with_methods(price, method, operand);

        return tick_size;
    }

    public Double calculation_with_methods(Double price, String method, Double operand){

        double res = 0.f;

        if(method.equals("floor")){
            res = Math.floor(price / operand) * operand;
        }else if(method.equals("round")){
            res = Math.round(price / operand) * operand;
        }else{
            res = Math.ceil(price / operand) * operand;
        }

        return (Double)res;

    }

    @Override
    public List<Double> make_levels(String mode, Double upper, Double lower, Integer grids) {

        String method = "floor";
        lower = get_tick_size(lower, method);
        upper = get_tick_size(upper, method);
        List<Double> levels = new ArrayList<Double>();
        levels.add(lower);
        int cnt = 1;

        Double diff = 0.0;
        Double price = 0.0;

        if(mode.equals("Arithmetic")){
            // 동일한 가격 차이로 levels 생성
            while(cnt < grids){
                diff =  (upper - lower)/(grids); // 주문들 사이의 가격 차이
                price = lower + diff * cnt;
                price = get_tick_size(price, method); // 업비트 호가에 맞춰서 조정
                levels.add(price);
                cnt += 1;
            }
        }else if(mode.equals("Geometric")){
            //동일한 가격 비율로 levels 생성
            while(cnt < grids){
                diff = (Double)Math.pow((upper / lower),(1.f/(grids))); //주문들 사이의 가격 비율 차이
                price = lower * (Double)Math.pow(diff,cnt);
                price = get_tick_size(price, method); // 업비트 호가에 맞춰서 조정
                levels.add(price);
                cnt += 1;
            }
        }

        // levels의 마지막은 그리드 차이
        levels.add(diff);
        return levels;
    }
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Double get_std(String coin, Integer interval, Integer stdNum) {

        List<Double> price_list = new ArrayList<Double>();

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("https://api.upbit.com/v1/candles/minutes/"+interval.toString()+"?market=KRW-"+coin+"&count="+stdNum.toString())
                //.url("https://api.upbit.com/v1/market/all?isDetails=false")
                .get()
                .addHeader("Accept", "application/json")
                .build();

        try{
            Response response = client.newCall(request).execute();
            //JSONObject jsonobj = new JSONObject(response.body().string());
            ResponseBody body = response.body();
            String str = body.string();
//            System.out.println("body = " + body.string());
            for(int i=0; i<stdNum; i++){
                Double trade_price = gson.fromJson(str, JsonArray.class)
                        .get(i)
                        .getAsJsonObject()
                        .get("trade_price")
                        .getAsDouble();
//            System.out.println(response.body().string());
//            System.out.println(res);
                System.out.println(trade_price);
                price_list.add(trade_price);
            }

            int n = price_list.size();
            Double sum = 0.0;

            for(Double i: price_list){
                sum += i;
            }

            Double m = sum/n;
            Double ss = 0.0;

            for(Double i: price_list){
                ss += Math.pow((i-m),2);
            }

            Double pvar = ss / n;

            return (Double)Math.pow(pvar, 0.5);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("response 실패");
        }

        return null;
    }

    @Override
    public ArrayList<Double> get_boundary(Double current, Double std, Double lowerStd) throws IllegalArgumentException {
        Double upper = current;
        Double lower = current - lowerStd*std;
        if (current < upper){
            throw new IllegalArgumentException("상한선은 현재가보다 낮아야합니다.");
        }
        ArrayList<Double> boundary = new ArrayList<Double>();
        boundary.add(upper);
        boundary.add(lower);
        return boundary;
    }

    @Override
    public Double get_volume(Double current, Double budget, Integer grids) throws IllegalArgumentException {
        double volume = Math.floor((budget / grids)/current*1e8)/1e8;
        if (volume * current < 5000){
            throw new IllegalArgumentException("최소 주문 단위보다 작습니다. "+(volume * current)%.2f+"원");
        }
        return (Double)volume;
    }
}

