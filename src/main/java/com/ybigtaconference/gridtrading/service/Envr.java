package com.ybigtaconference.gridtrading.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Envr {

    String mode, coin;
    Integer grids;
    Float budget, scale, std, upper, lower, diff, volume, stop_loss;
    List<Float> levels;


    public Envr() {
    }

    public Envr(Params params) {
        this.mode = params.mode;
        this.coin = params.coin;
        this.grids = params.grids;
        this.budget = params.budget;
        this.lower = params.lower;
        this.stop_loss = params.stop_loss;
    }

}
