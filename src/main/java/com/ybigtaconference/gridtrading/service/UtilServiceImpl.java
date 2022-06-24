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
    public Float get_price_scale_tick(Float price) {

        Float res = 0.01f;

        if (price >= 2000000){
            res = 1000.f;
        }else if(price >= 1000000){
            res = 500.f;
        }else if(price >= 500000){
            res = 100.f;
        }else if(price >= 100000){
            res = 50.f;
        }else if(price >= 10000){
            res = 10.f;
        }else if(price >= 1000){
            res = 5.f;
        }else if(price >= 100){
            res = 1.f;
        }else if(price >= 10){
            res = 0.1f;
        }else if(price >= 0){
            res = 0.01f;
        }

        return res;
    }

    @Override
    public Float get_tick_size(Float price, String method) {
        /* 원화마켓 주문 가격 단위

        Args:
            price (float]): 주문 가격
            method (str, optional): 주문 가격 계산 방식. Defaults to "floor".

        Returns:
            float: 업비트 원화 마켓 주문 가격 단위로 조정된 가격
        */

        // tick_size
        Float tick_size = 0.f;
        Float operand = 0.01f;

        if (price >= 2000000){
            operand = 1000.f;
        }else if(price >= 1000000){
            operand = 500.f;
        }else if(price >= 500000){
            operand = 100.f;
        }else if(price >= 100000){
            operand = 50.f;
        }else if(price >= 10000){
            operand = 10.f;
        }else if(price >= 1000){
            operand = 5.f;
        }else if(price >= 100){
            operand = 1.f;
        }else if(price >= 10){
            operand = 0.1f;
        }else if(price >= 0){
            operand = 0.01f;
        }


        tick_size = calculation_with_methods(price, method, operand);

        return tick_size;
    }

    public Float calculation_with_methods(Float price, String method, Float operand){

        double res = 0.f;

        if(method.equals("floor")){
            res = Math.floor(price / operand) * operand;
        }else if(method.equals("round")){
            res = Math.round(price / operand) * operand;
        }else{
            res = Math.ceil(price / operand) * operand;
        }

        return (float)res;

    }

    @Override
    public List<Float> make_levels(String mode, Float upper, Float lower, Integer grids) {

        String method = "floor";
        lower = get_tick_size(lower, method);
        upper = get_tick_size(upper, method);
        List<Float> levels = new ArrayList<Float>();
        int cnt = 1;

        Float diff = 0.f;
        Float price = 0.f;

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
                diff = (float)Math.pow((upper / lower),(1/(grids))); //주문들 사이의 가격 비율 차이
                price = lower * (float)Math.pow(diff,cnt);
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
    public Float get_std(String coin, Integer interval, Integer stdNum) {

        List<Float> price_list = new ArrayList<Float>();

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
                Float trade_price = gson.fromJson(str, JsonArray.class)
                        .get(i)
                        .getAsJsonObject()
                        .get("trade_price")
                        .getAsFloat();
//            System.out.println(response.body().string());
//            System.out.println(res);
                System.out.println(trade_price);
                price_list.add(trade_price);
            }

            int n = price_list.size();
            float sum = 0;

            for(float i: price_list){
                sum += i;
            }

            float m = sum/n;
            float ss = 0;

            for(float i: price_list){
                ss += Math.pow((i-m),2);
            }

            float pvar = ss / n;

            return (float)Math.pow(pvar, 0.5);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("response 실패");
        }

        return null;
    }

    @Override
    public ArrayList<Float> get_boundary(Float current, Float std, Float lowerStd) throws IllegalArgumentException {
        Float upper = current;
        Float lower = current - lowerStd*std;
        if (current < upper){
            throw new IllegalArgumentException("상한선은 현재가보다 낮아야합니다.");
        }
        ArrayList<Float> boundary = new ArrayList<Float>();
        boundary.add(upper);
        boundary.add(lower);
        return boundary;
    }

    @Override
    public Float get_volume(Float current, Float budget, Integer grids) throws IllegalArgumentException {
        double volume = Math.floor((budget / grids)/current*1e8)/1e8;
        if (volume * current < 5000){
            throw new IllegalArgumentException("최소 주문 단위보다 작습니다. "+(volume * current)%.2f+"원");
        }
        return (float)volume;
    }
}

