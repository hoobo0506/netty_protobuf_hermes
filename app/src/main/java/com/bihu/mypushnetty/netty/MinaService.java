package com.bihu.mypushnetty.netty;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermes.HermesService;

/**
 * Created by 25623 on 2017/8/1.
 */

public class MinaService extends HermesService.HermesService1 {
    @Override
    public void onCreate() {
        super.onCreate();
        Hermes.register(ChannelManager.class);
    }
}
