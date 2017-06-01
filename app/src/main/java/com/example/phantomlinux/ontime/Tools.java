package com.example.phantomlinux.ontime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by phantomlinux on 10/20/2015.
 */


class Tools {
    static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null) { // connected to the internet
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                haveConnectedWifi = true; // connected to wifi
            } else if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                haveConnectedMobile = true; //connected to 4g
            }
        }
        return haveConnectedMobile || haveConnectedWifi ;
    }

    static String getDataDir(final Context context) throws Exception {
        return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.dataDir;
    }
}
