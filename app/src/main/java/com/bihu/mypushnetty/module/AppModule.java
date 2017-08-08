package com.bihu.mypushnetty.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.bihu.mypushnetty.app.Constant;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreference() {
        return context.getSharedPreferences(Constant.SHAREDPREFERENCE_FILE, Context.MODE_PRIVATE);
    }
    @Singleton
    @Provides
    public ExecutorService provideExecutorService() {
        return Executors.newFixedThreadPool(2);

    }

    @Singleton
    @Provides
    public ScheduledExecutorService provideScheduledExecutorService() {
        return Executors.newScheduledThreadPool(5);
    }
}