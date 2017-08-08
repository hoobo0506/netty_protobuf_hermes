package com.bihu.mypushnetty.component;

import com.bihu.mypushnetty.module.AppModule;
import com.bihu.mypushnetty.netty.ChannelManager;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Injector 方式
 */
@Singleton
@Component(modules = {AppModule.class })
public interface AppComponentInjector {
    void inject(ChannelManager manager);
}