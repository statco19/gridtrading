package com.ybigtaconference.gridtrading.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Getter @Setter
@Slf4j
public class UpbitImpl implements Upbit {

    private String accessKey,secretKey;
    private static final String serverUrl = "https://api.upbit.com";
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public UpbitImpl() {
    }

    public UpbitImpl(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public String get_balances() {
        log.info("accessKey {}", this.accessKey);
        log.info("secretKey {}", this.secretKey);
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .sign(algorithm);


        String authenticationToken = "Bearer " + jwtToken;
        //
        String res;
        try {
//                HttpClient client = HttpClientBuilder.create().build();
                CloseableHttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(serverUrl + "/v1/accounts");
                request.setHeader("Content-Type", "application/json");
                request.addHeader("Authorization", authenticationToken);

//                HttpResponse response = client.execute(request);
                CloseableHttpResponse response = client.execute(request);
                //
                HttpEntity entity = response.getEntity();

                res = EntityUtils.toString(entity, "UTF-8");


//                System.out.println(EntityUtils.toString(entity, "UTF-8"));
                log.info("commit_order {}", res);


            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        return res;
    }

    @Override
    public String get_balance(String coin) {
        try {
            String balances = this.get_balances();
            JsonArray jsonArray = gson.fromJson(balances, JsonArray.class).getAsJsonArray();
            for (JsonElement jsonElement : jsonArray) {
                String currency = jsonElement.getAsJsonObject().get("currency").getAsString();
                if (currency.equals(coin)) {
                    String volume = jsonElement.getAsJsonObject().get("balance").getAsString();
                    return volume;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String get_order_uuid(String uuid) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", uuid);

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        String res;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(serverUrl + "/v1/order?" + queryString);
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            res = EntityUtils.toString(entity, "UTF-8");
            log.info("get_order_uuid {}", res);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return res;
    }

    @Override
    public String get_order(String ticker) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", ticker);
        params.put("state", "wait");
        params.put("page", "1");
        params.put("limit", "100");
        params.put("order_by", "desc");

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
            queryElements.add(entity.getKey() + "=" + entity.getValue());
        }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;

        String res;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(serverUrl + "/v1/orders?" + queryString);
            request.setHeader("Content-Type", "application/json");
            request.addHeader("Authorization", authenticationToken);

            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();

            res = EntityUtils.toString(entity, "UTF-8");

//          System.out.println(EntityUtils.toString(entity, "UTF-8"));
            log.info("get_order {}", res);

//            System.out.println(EntityUtils.toString(entity, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return res;
    }

    @Override
    public String cancel_order(String uuid) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", uuid);

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
                queryElements.add(entity.getKey() + "=" + entity.getValue());
            }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;
        //
        String res;
        try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpDelete request = new HttpDelete(serverUrl + "/v1/order?" + queryString);
                request.setHeader("Content-Type", "application/json");
                request.addHeader("Authorization", authenticationToken);

                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();

                res = EntityUtils.toString(entity, "UTF-8");


//                System.out.println(EntityUtils.toString(entity, "UTF-8"));
                log.info("cancel_order {}", res);

//                System.out.println(EntityUtils.toString(entity, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        return res;
    }

    @Override
    public String order(String ticker, Double price, Double volume, String side, String ord_type) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        HashMap<String, String> params = new HashMap<>();
        params.put("market", ticker);
        params.put("side", side);
        params.put("volume", volume.toString());
        params.put("ord_type", ord_type);
        if(ord_type.equals("limit")) {
            params.put("price", price.toString());
        }

        ArrayList<String> queryElements = new ArrayList<>();
        for(Map.Entry<String, String> entity : params.entrySet()) {
                queryElements.add(entity.getKey() + "=" + entity.getValue());
            }

        String queryString = String.join("&", queryElements.toArray(new String[0]));

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(queryString.getBytes("UTF-8"));

        String queryHash = String.format("%0128x", new BigInteger(1, md.digest()));

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        String jwtToken = JWT.create()
                .withClaim("access_key", accessKey)
                .withClaim("nonce", UUID.randomUUID().toString())
                .withClaim("query_hash", queryHash)
                .withClaim("query_hash_alg", "SHA512")
                .sign(algorithm);

        String authenticationToken = "Bearer " + jwtToken;
        //
        String res;
        try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(serverUrl + "/v1/orders");
                request.setHeader("Content-Type", "application/json");
                request.addHeader("Authorization", authenticationToken);
                request.setEntity(new StringEntity(new Gson().toJson(params)));

                HttpResponse response = client.execute(request);
                HttpEntity entity = response.getEntity();
                res = EntityUtils.toString(entity, "UTF-8");


//                System.out.println(EntityUtils.toString(entity, "UTF-8"));
                log.info("commit_order {}", res);
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }

        return res;
    }

    @Override
    public String get_current_price(String ticker) throws IOException {
        OkHttpClient client = new OkHttpClient();

        try {
            Request request = new Request.Builder()
                    .url("https://api.upbit.com/v1/trades/ticks?market="+ ticker +"&count=1")
                    .get()
                    .addHeader("Accept", "application/json")
                    .build();

            Response response = client.newCall(request).execute();
            String str = response.body().string();
            String trade_price = gson.fromJson(str, JsonArray.class)
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .get("trade_price")
                    .getAsString();

            return trade_price;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
