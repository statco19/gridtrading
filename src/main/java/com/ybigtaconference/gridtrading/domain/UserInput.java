package com.ybigtaconference.gridtrading.domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserInput {
    String accessKey;
    String secretKey;
    Float budget;
    Float stopLoss;
    Integer gridNum;

    public UserInput() {
    }

    public UserInput(String accessKey, String secretKey, Float budget, Float stopLoss, Integer gridNum) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.budget = budget;
        this.stopLoss = stopLoss;
        this.gridNum = gridNum;
    }
}
