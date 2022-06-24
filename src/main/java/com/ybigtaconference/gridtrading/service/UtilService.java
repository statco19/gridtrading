package com.ybigtaconference.gridtrading.service;

import java.util.List;

public interface UtilService {
    public Double get_price_scale_tick(Double price);
    public Double get_tick_size(Double price, String method);
    public Double calculation_with_methods(Double price, String method, Double operand);
    public List<Double> make_levels(String mode, Double upper, Double lower, Integer grids);
    public Double get_std(String coin, Integer interval, Integer stdNum);
    public List<Double> get_boundary(Double current, Double std, Double lowerStd);
    public Double get_volume(Double current, Double budget, Integer grids);
}
