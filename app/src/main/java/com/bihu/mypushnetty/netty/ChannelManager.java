package com.bihu.mypushnetty.netty;

import android.content.Context;
import android.util.Log;

import com.bihu.mypushnetty.component.DaggerAppComponentInjector;
import com.bihu.mypushnetty.module.AppModule;
import com.bihu.mypushnetty.utils.AppUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import xiaofei.library.hermes.annotation.ClassId;
import xiaofei.library.hermes.annotation.GetInstance;
import xiaofei.library.hermes.annotation.MethodId;

/**
 * Created by hoobo on 2017/7/28.
 */
@ClassId("ChannelManager")
public class ChannelManager {
    private static final String TAG = "ChannelManager";
    private static ChannelManager mInstance = null;
    String sid = "-1";
    @Inject
    ExecutorService mExcutor;
    private Bootstrap mBootstrap;
    private int mCount = 0;
    private Channel mChannel;
    private ChannelFutureListener channelFutureListener;

    private boolean hasInited = false;

    private ChannelManager() {
    }

    @MethodId("writeToServer")
    public void writeToServer(Object msg) {
        Driver.ClientMessage message = (Driver.ClientMessage) msg;
        if (getChannel() == null) {
            Log.e("tag", "客户端准备发送消息，但是mChannel null");
            getChannel().write(msg);
        } else if (!(getChannel().isOpen() && getChannel().isActive())) {
            Log.e("tag", "客户端准备发送消息,但是未连接" + message.toString());
        } else {
            Log.e("tag", "客户端准备发送消息：" + msg.toString());
            getChannel().write(msg);
        }
    }

    @MethodId("getChannel")
    private Channel getChannel() {
        return mChannel;
    }

    @MethodId("setChannel")
    private void setChannel(Channel channel) {
        this.mChannel = channel;
        Log.e(TAG, "pid:" + android.os.Process.myPid() + "..." + channel.isActive());
    }

    @MethodId("closeChannel")
    public void closeChannel() {
        if (mChannel != null) {
            mChannel.close();
        }
    }

    @MethodId("removeChannel")
    public void removeChannel() {
        this.mChannel = null;
    }

    @MethodId("init")
    public void init(Context context) {
        if (!hasInited) {
            channelFutureListener = f -> {
                if (f.isSuccess()) {
                    Log.d(TAG, "重新连接服务器成功" + mCount);
                    Log.d(TAG, f.channel().isOpen() + ":" + f.channel().toString());
                    ChannelManager.this.setChannel(f.channel());
                    //这边需要写登陆socket的代码
                    Log.e(TAG, AppUtils.getPesudoUniqueID());
                    Driver.ClientMessage build = Driver.ClientMessage.newBuilder().setType(1).setHeartBeat(Driver
                            .HeartBeat.newBuilder().setSid(ChannelManager.getInstance().getSid()).setImei(AppUtils
                                    .getPesudoUniqueID()).build()).build();
                    f.channel().writeAndFlush(build);
                } else {
                    Log.d(TAG, "重新连接服务器失败" + mCount);
                    //  3秒后重新连接
                    f.channel().eventLoop().schedule(ChannelManager.this::doConnect, 3, TimeUnit.SECONDS);
                }
                mCount++;
            };
            DaggerAppComponentInjector.builder().appModule(new AppModule(context)).build().inject(this);
            NioEventLoopGroup group = new NioEventLoopGroup();
            mBootstrap = new Bootstrap().remoteAddress(Constants.SocketConfig.HOST, Constants.SocketConfig.PORT);
            mBootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new DriverClientInitializer())
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_TIMEOUT, 5000);
            mExcutor.execute(this::doConnect);
            hasInited = true;
        } else {
            Log.e(TAG, "dont init twice");
        }
    }

    public String getSid() {
        return sid;
    }

    @GetInstance
    public static ChannelManager getInstance() {
        if (mInstance == null) {
            mInstance = new ChannelManager();
        }
        return mInstance;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @MethodId("doConnect")
    public void doConnect() {
        mBootstrap.connect().addListener(channelFutureListener);
    }

}
