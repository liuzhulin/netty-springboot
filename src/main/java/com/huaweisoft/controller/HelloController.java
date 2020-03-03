package com.huaweisoft.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloController {

    @GetMapping("hello")
    public String hello() {
        String data = "1";
        log.info("nihao{data}", data);
        return "hello abc";
    }
}
