package com.uceku.ucekustudy;

import android.app.Application;

import com.novoda.merlin.Bindable;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.NetworkStatus;
import com.uceku.ucekustudy.network.NetworkConfig;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MyApplication extends Application implements Connectable, Disconnectable, Bindable {

    Merlin merlin;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        merlin = new Merlin.Builder().withAllCallbacks().build(getBaseContext());
        merlin.registerConnectable(this);
        merlin.registerDisconnectable(this);
        merlin.registerBindable(this);
        merlin.bind();
    }

    @Override
    public void onBind(NetworkStatus networkStatus) {
        if (networkStatus.isAvailable()) NetworkConfig.setNetworkConnected(true);
        else NetworkConfig.setNetworkConnected(false);
    }

    @Override
    public void onConnect() {
        NetworkConfig.setNetworkConnected(true);
    }

    @Override
    public void onDisconnect() {
        NetworkConfig.setNetworkConnected(false);
    }
}
