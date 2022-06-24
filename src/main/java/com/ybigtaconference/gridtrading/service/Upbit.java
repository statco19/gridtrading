package com.ybigtaconference.gridtrading.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface Upbit {
    public String get_balances();
    public String get_balance(String coin);
    public String get_order(String ticker) throws NoSuchAlgorithmException, UnsupportedEncodingException;
    public String cancel_order(String uuid) throws NoSuchAlgorithmException, UnsupportedEncodingException;
    public String order(String ticker, Float price, Float volume, String side, String ord_type) throws NoSuchAlgorithmException, UnsupportedEncodingException;
    public String get_current_price(String ticker) throws IOException;
}
