package com.ybigtaconference.gridtrading.service;

import java.util.List;

public interface UtilService {
    public Float get_price_scale_tick(Float price);
    public Float get_tick_size(Float price, String method);
    public List<Float> make_levels(String mode, Float upper, Float lower, Integer grids);
    public Float get_std(String coin, Integer interval, Integer stdNum);
    public List<Float> get_boundary(Float current, Float std, Float lowerStd);
    public Float get_volume(Float current, Float budget, Integer grids);
}
