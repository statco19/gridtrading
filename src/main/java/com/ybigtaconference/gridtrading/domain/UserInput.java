package com.ybigtaconference.gridtrading.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserInput {
    String accessKey;
    String secretKey;
    Double budget;
    Double stopLoss;
    Double lower;
    Integer gridNum;

    public UserInput() {
    }

    public UserInput(String accessKey, String secretKey, Double budget, Double stopLoss, Integer gridNum, Double lower) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.budget = budget;
        this.stopLoss = stopLoss;
        this.gridNum = gridNum;
        this.lower = lower;
    }
}
