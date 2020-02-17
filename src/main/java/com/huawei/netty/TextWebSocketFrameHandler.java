package com.huawei.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("[" + ctx.channel().id().asLongText() + "]" + msg.text());
        //回复消息
        //ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器当前时间：" + LocalDateTime.now() + " " + msg.text()));
        clients.writeAndFlush(new TextWebSocketFrame("[" + LocalDateTime.now() + "]" + msg.text()));
    }

    //web客户端连接后，触发该方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        //id表示唯一的值，longText是唯一的
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
        //shortText不一定是唯一的
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());

        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("handlerRemoved 被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
