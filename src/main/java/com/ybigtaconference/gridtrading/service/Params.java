package com.ybigtaconference.gridtrading.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Params {

    String mode, coin;
    Integer grids;
    Float budget, lower, stop_loss;

    public Params() {
    }

    public Params(Params params) {
        params.mode = this.mode;
        params.coin = this.coin;
        params.grids = this.grids;
        params.budget = this.budget;
        params.lower = this.lower;
        params.stop_loss = this.stop_loss;
    }
}
