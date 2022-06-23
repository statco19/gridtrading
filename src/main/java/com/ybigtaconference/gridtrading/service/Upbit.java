package com.ybigtaconference.gridtrading.service;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Upbit {

    private String accessKey,secretKey;

    public Upbit(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    
}
