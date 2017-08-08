/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.bihu.mypushnetty.netty;

import android.util.Log;

import com.bihu.mypushnetty.utils.AppUtils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import xiaofei.library.hermeseventbus.HermesEventBus;

/**
 *
 */
class DriverClientHandler extends SimpleChannelInboundHandler<Driver.ClientMessage> {
    private static final String TAG = "DriverClientHandler";
    Logger LOG = Logger.getLogger("DriverClientHandler");
    DriverClientHandler() {
        super(false);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, Driver.ClientMessage clientMessage) {
        Log.e("messageReceived", clientMessage.toString());
        switch (clientMessage.getType()) {//1=socket心跳，2=退出领取红包，3=领取红包消息，4=GPS位置信息，5=司机端消息
            case Constants.MessageTypes.LOGIN:
                Driver.HeartBeat heartBeat = clientMessage.getHeartBeat();
                Log.e(TAG,heartBeat.toString());
                String sid = heartBeat.getSid();
                ChannelManager.getInstance().setSid(sid);
                HermesEventBus.getDefault().post(heartBeat);
                break;
            case Constants.MessageTypes.LOGOUT:
                break;
            case Constants.MessageTypes.REDPACKTET:
                Driver.RedPacket redPacket = clientMessage.getRedPacket();
                HermesEventBus.getDefault().post(redPacket);
                break;
            case Constants.MessageTypes.GPSINFO:
                break;
            case Constants.MessageTypes.DRIVERMESSAGE:
                Driver.DriverMsg driverMsg = clientMessage.getDriverMsg();
                HermesEventBus.getDefault().post(driverMsg);
                break;
            default:
                break;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.log(Level.INFO,"---exceptionCaught---",cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.d(TAG, "与服务器断开连接服务器");
        super.channelInactive(ctx);
        ChannelManager.getInstance().removeChannel();

        //重新连接服务器
        ctx.channel().eventLoop().schedule(() -> ChannelManager.getInstance().doConnect(), 3, TimeUnit.SECONDS);
        ctx.close();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == io.netty.handler.timeout.IdleState.WRITER_IDLE) {
                Driver.ClientMessage build = Driver.ClientMessage.newBuilder().setType(2).setHeartBeat(Driver
                        .HeartBeat.newBuilder().setSid(ChannelManager.getInstance().getSid()).setImei(AppUtils
                                .getPesudoUniqueID()).build()).build();
                ctx.writeAndFlush(build);
                Log.e(TAG, "heartbeat:" + build.toString());

            }
        }else {
            Log.e(TAG,"userEventTriggered,evt:"+evt.toString());
        }
    }

}
