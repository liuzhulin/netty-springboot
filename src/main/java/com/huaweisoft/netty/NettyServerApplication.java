package com.huaweisoft.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NettyServerApplication {

    @Value("${netty.port}")
    private String port;

    @Autowired
    @Qualifier(value = "serverBootstrap")
    ServerBootstrap serverBootstrap;

    private ChannelFuture channelFuture;

    public void start() throws InterruptedException {
        channelFuture = serverBootstrap.bind(Integer.parseInt(port)).sync();
        System.out.println("netty 启动成功");
    }

    public void stop() {
        channelFuture.channel().close();
    }
}
