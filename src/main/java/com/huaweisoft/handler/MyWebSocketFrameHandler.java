package com.huaweisoft.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@ChannelHandler.Sharable
public class MyWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("[" + ctx.channel().id().asLongText() + "]" + msg.text());
        //回复消息
        Channel iChannel = ctx.channel();

        for (Channel channel : clients) {
            if (channel == iChannel) {
                channel.writeAndFlush(new TextWebSocketFrame("自己[" + LocalDateTime.now() + "]" + msg.text()));
            } else {
                channel.writeAndFlush(new TextWebSocketFrame("别人[" + LocalDateTime.now() + "]" + msg.text()));
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        //id表示唯一的值，longText是唯一的
        System.out.println("handlerAdded 被调用，id：" + ctx.channel().id().asLongText());

        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("handlerRemoved 被调用，id：" + ctx.channel().id().asLongText());
    }

    @Override
public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }
}
