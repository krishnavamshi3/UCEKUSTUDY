package com.uceku.ucekustudy.network;


public class NetworkConfig {
    private static boolean NETWORK_CONNECTED  = false;

    public static boolean isNetworkConnected() {
        return NETWORK_CONNECTED;
    }

    public static void setNetworkConnected(boolean networkConnected) {
        NETWORK_CONNECTED = networkConnected;
    }
}
