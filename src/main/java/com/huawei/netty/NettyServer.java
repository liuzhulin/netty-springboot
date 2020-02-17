package com.huawei.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

@Component
public class NettyServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;

    private static class SingletonWebSocketServer {
        static final NettyServer instance = new NettyServer();
    }

    public static NettyServer getInstance() {
        return SingletonWebSocketServer.instance;
    }

    public NettyServer() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap()
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
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
                         * 2.浏览器请求时 ws://localhost:7000/hello，表示请求的uri
                         * 3.WebSocketServerProtocolHandler核心功能是将http协议升级为websocket协议保持长连接
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                        //自定义handler，处理业务逻辑
                        pipeline.addLast(new TextWebSocketFrameHandler());
                    }
                });
    }

    public void start(int port) {
        bootstrap.bind(port);
        System.out.println("netty server 启动完毕");
    }

}
