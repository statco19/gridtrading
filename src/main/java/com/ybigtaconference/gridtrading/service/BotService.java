package com.ybigtaconference.gridtrading.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface BotService {
    public void connect_upbit();
    public void cancel_all_order(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    public void sell_all_market(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    public Double get_avg(String coin);
    public Map<String, List<Double>> get_open_price(String coin) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    public void levels_order(String coin, Double volume, List<Double> levels) throws UnsupportedEncodingException, NoSuchAlgorithmException;
    public Envr set_env(Params params) throws Exception;
    public void trade(Envr env) throws IOException, NoSuchAlgorithmException, InterruptedException;

}
