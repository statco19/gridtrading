package com.ybigtaconference.gridtrading.testtest;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class test_ {

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {

        OkHttpClient client = new OkHttpClient();

        Integer interval = 30;
        String coin = "BTC";
        Integer stdNum = 20;

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
            }

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("response 실패");
        }


    }
}