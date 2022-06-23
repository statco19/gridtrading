package com.ybigtaconference.gridtrading.service;

import org.apache.http.HttpEntity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public interface Upbit {
    public String get_balance();
    public String get_order(String ticker) throws NoSuchAlgorithmException, UnsupportedEncodingException;
    public String cancel_order(String uuid) throws NoSuchAlgorithmException, UnsupportedEncodingException;
    public String order(String ticker, Float price, Float volume, String side, String ord_type) throws NoSuchAlgorithmException, UnsupportedEncodingException;

}
