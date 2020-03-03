package com.huaweisoft.controller;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("k1", "v1");
        map.put("k2", null);
        String abd = "arg";
        String ss = (String)map.computeIfPresent("k2", (k, v) -> {
           return k + v;
        });
        System.out.println("ss = " + ss);
        System.out.println(map);
    }
}
