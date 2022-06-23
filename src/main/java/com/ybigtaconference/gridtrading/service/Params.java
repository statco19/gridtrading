package com.ybigtaconference.gridtrading.service;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Params {

    String mode, coin;
    Integer grids, interval, std_num;
    Float budget, lower_std, reset_grid, stop_loss;

}
