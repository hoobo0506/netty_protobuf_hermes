package com.bihu.mypushnetty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.bihu.mypushnetty.netty.ChannelManager;
import com.bihu.mypushnetty.netty.Driver;
import com.bihu.mypushnetty.netty.IChannelManager;
import com.bihu.mypushnetty.netty.MinaService;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import xiaofei.library.hermes.Hermes;
import xiaofei.library.hermes.HermesListener;
import xiaofei.library.hermes.HermesService;
import xiaofei.library.hermeseventbus.HermesEventBus;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    IChannelManager instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HermesEventBus.getDefault().register(this);
        Hermes.register(ChannelManager.class);
        Hermes.setHermesListener(new HermesListener() {
            @Override
            public void onHermesConnected(Class<? extends HermesService> service) {
                instance = Hermes.getInstanceInService(service, IChannelManager.class);
                instance.init(MainActivity.this);
                Log.e(TAG, ((Boolean)(Hermes.isConnected(service))).toString());
                Log.e(TAG, "pid:" + android.os.Process.myPid());
            }
        });
        Hermes.connect(getApplicationContext(), MinaService.class);
    }
    public void onTestClick(View v){
        Driver.ClientMessage build = Driver.ClientMessage.newBuilder().setType(5).setGpsInfo(Driver
                .GpsInfo.newBuilder().setDeviceId("胡博测试")
                .setLat("lathehe").setLng("lnghaha").build()).build();
        if (instance!=null){
            instance.writeToServer(build);
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(Driver.HeartBeat heartBeat){
        Log.e(TAG,heartBeat.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HermesEventBus.getDefault().unregister(this);
    }
}
