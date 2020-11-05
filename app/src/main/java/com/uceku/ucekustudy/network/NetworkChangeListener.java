package com.uceku.ucekustudy.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeListener extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        NetworkConfig.setNetworkConnected(checkInternet(context));

    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }
}
