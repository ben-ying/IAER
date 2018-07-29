package com.yjh.iaer.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    public static final String TAG = "NetworkConnectChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (manager != null) {
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null) {
                    if (activeNetwork.isConnected()) {
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            Log.e(TAG, "Connected Wifi");
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            Log.e(TAG, "Connected Mobile");
                        }
                    } else {
                        Log.e(TAG, "Network not connected");
                    }


                    Log.e(TAG, "info.getTypeName()" + activeNetwork.getTypeName());
                    Log.e(TAG, "getSubtypeName()" + activeNetwork.getSubtypeName());
                    Log.e(TAG, "getState()" + activeNetwork.getState());
                    Log.e(TAG, "getDetailedState()"
                            + activeNetwork.getDetailedState().name());
                    Log.e(TAG, "getDetailedState()" + activeNetwork.getExtraInfo());
                    Log.e(TAG, "getType()" + activeNetwork.getType());
                } else {
                    Log.e(TAG, "No Active Network");

                }
            }
        }
    }
}