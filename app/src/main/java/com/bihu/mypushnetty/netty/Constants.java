package com.bihu.mypushnetty.netty;

/**
 * Created by hoobo on 2017/7/6.
 */

public class Constants {

    static class SocketConfig {
        static final String HOST = "t214.socket.behuh.com";
        static final int PORT = 9988;
    }

    static class MessageTypes {
        static final int LOGIN = 1;
        static final int LOGOUT = 2;
        static final int REDPACKTET = 3;
        static final int GPSINFO = 4;
        static final int DRIVERMESSAGE = 5;
    }

}
