package com.uceku.ucekustudy;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import io.realm.Realm;

public class MyDownloadManagerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //Fetching the download id received with the broadcast
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        //Checking if the received broadcast is for our enqueued download by matching download id
        onReceiveDownloadComplete(id);
    }

    private void onReceiveDownloadComplete(long downloadID) {
        Realm realm = Realm.getDefaultInstance();
        // Is it notes download

    }
}
