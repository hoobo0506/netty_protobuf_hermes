package com.bihu.mypushnetty.netty;

import android.content.Context;

import io.netty.channel.Channel;
import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.MethodId;

@ClassId("ChannelManager")
public interface IChannelManager {
    @MethodId("setChannel")
    void setChannel(Channel channel);

    @MethodId("getChannel")
    Channel getChannel(Channel channel);

    @MethodId("removeChannel")
    void removeChannel();

    @MethodId("closeChannel")
    void closeChannel();

    @MethodId("doConnect")
    void doConnect();

    @MethodId("init")
    void init(Context context);

    @MethodId("writeToServer")
    void writeToServer(Object msg);
}