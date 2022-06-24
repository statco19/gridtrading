package com.ybigtaconference.gridtrading.service.test;

import com.ybigtaconference.gridtrading.credentials.TokenAndKey;
import com.ybigtaconference.gridtrading.service.UpbitImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RequiredArgsConstructor
public class orderTest {

    private static final UpbitImpl upbit = new UpbitImpl(TokenAndKey.ACCESS_KEY, TokenAndKey.SECRET_KEY);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        String res = upbit.get_order("KRW-BTC");
        log.info("test {}", res);
    }
}
