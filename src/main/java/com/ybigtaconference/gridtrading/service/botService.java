package com.ybigtaconference.gridtrading.service;

import java.util.List;
import java.util.Map;

public interface botService {
    public void connect_upbit(String key_path);
    public void cancel_all_order(String coin);
    public void sell_all_market(String coin);
    public Float get_avg(String coin);
    public Map<String, List<Float>> get_open_price(String coin);
    public void levels_order(String coin, Float volume, List<Float> levels);
    public Envr set_env(Params params);
    public void trade(Envr env);

}
