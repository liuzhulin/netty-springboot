package com.huaweisoft;

import com.huaweisoft.netty.NettyServerApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringbootApplication {
    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(SpringbootApplication.class);
        NettyServerApplication nettyServerApplication = context.getBean(NettyServerApplication.class);
        nettyServerApplication.start();
    }
}
