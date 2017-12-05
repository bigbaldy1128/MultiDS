package com.qihoo.multids.sharding.algorithm;

import lombok.Data;

import java.util.List;

@Data
public class MigrationData {
    private String from;
    private String to;
    private List<Object> keys;
}
