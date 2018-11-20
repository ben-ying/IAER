package com.yjh.iaer.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.List;

public class SystemUtils {
    private static final String TAG = "SystemUtils";

    public static void showKeyboard(Activity activity, EditText editText) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager keyboard = (InputMethodManager)
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (keyboard != null) {
                keyboard.showSoftInput(editText, 0);
            }
        }
    }

    public static boolean isLocalServer(Context context) {
        String ssid = getCurrentSsid(context);
        Log.d(TAG, "ssid: " + ssid);
        return ssid.equals("\"ben\"") || ssid.equals("ben");
    }

    private static String getCurrentSsid(Context context) {
        String ssid = "";
        ConnectivityManager connManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            switch (networkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    final WifiManager wifiManager = (WifiManager)
                            context.getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager != null) {
                        final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                        if (connectionInfo != null &&
                                connectionInfo.getSupplicantState() == SupplicantState.COMPLETED) {
                            int networkId = connectionInfo.getNetworkId();
                            List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
                            for (WifiConfiguration wifiConfiguration : wifiConfigurations) {
                                if (wifiConfiguration.networkId == networkId) {
                                    ssid = wifiConfiguration.SSID;
                                    break;
                                }
                            }
                        }
                    }
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    Log.d(TAG, "Connected to mobile data");
                    break;
                default:
                    break;
            }
        }

        return ssid;
    }
}



