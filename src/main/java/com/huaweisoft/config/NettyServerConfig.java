package com.huaweisoft.config;

import com.huaweisoft.handler.MetricHandler;
import com.huaweisoft.handler.MyWebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyServerConfig {

    @Value("${netty.url}")
    private String nettyUrl;

    @Bean
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    @Bean(name = "serverBootstrap")
    public ServerBootstrap serverBootstrap() {
        return new ServerBootstrap()
            .group(bossGroup(), workerGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    //因为基于http协议，所以需要http的编解码器
                    pipeline.addLast(new HttpServerCodec());
                    //以块方式写，添加相应处理器
                    pipeline.addLast(new ChunkedWriteHandler());
                    //http在传输过程是分段传输的，HttpObjectAggregator就是将其聚合
                    pipeline.addLast(new HttpObjectAggregator(1024 * 4));
                    /**
                     * 1.对应websocket，它的数据是以帧形式传递
                     * 2.浏览器请求时 ws://localhost:端口/hello，表示请求的uri
                     * 3.WebSocketServerProtocolHandler核心功能是将http协议升级为websocket协议保持长连接
                     */
                    pipeline.addLast(new WebSocketServerProtocolHandler(nettyUrl));
                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                    pipeline.addLast("metricHandler", new MetricHandler());
                    //自定义handler，处理业务逻辑
                    pipeline.addLast(new MyWebSocketFrameHandler());
                }
            });
    }
}
