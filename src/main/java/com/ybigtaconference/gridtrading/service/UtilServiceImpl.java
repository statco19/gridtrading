package com.ybigtaconference.gridtrading.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilServiceImpl implements UtilService{
    @Override
    public Float get_price_scale_tick(Float price) {
        return null;
    }

    @Override
    public Float get_tick_size(Float price, String method) {
        return null;
    }

    @Override
    public List<Float> make_levels(String mode, Float upper, Float lower, Integer grids) {
        return null;
    }

    @Override
    public Float get_std(String coin, Integer interval, Integer stdNum) {
        return null;
    }

    @Override
    public List<Float> get_boundary(Float current, Float std, Float lowerStd) {
        return null;
    }

    @Override
    public Float get_volume(Float current, Float budget, Integer grids) {
        return null;
    }
}
