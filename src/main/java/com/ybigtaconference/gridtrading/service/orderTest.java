package com.ybigtaconference.gridtrading.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RequiredArgsConstructor
public class orderTest {

    private static final UpbitImpl upbit = new UpbitImpl();

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        upbit.setAccessKey("your_access_key");
        upbit.setSecretKey("your_secret_key");

        String res = upbit.order("KRW-BTC", 30000000f, 0.0002f, "bid", "limit");

        log.info("order response {}", res);
    }
}
