package v;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    k activity;
    WifiP2pManager.Channel channel;
    WifiP2pManager manager;

    public WiFiDirectBroadcastReceiver() {}

    WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, k activity){
        super();
        this.activity = activity;
        this.channel = channel;
        this.manager = manager;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if(activity == null || manager==null) return;
        switch (intent.getAction()){
            case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    activity.setIsWifiP2pEnabled(true);
                } else {
                    activity.setIsWifiP2pEnabled(false);
                }
                break;
            case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                if (manager != null) {
                    manager.requestPeers(channel, activity.peerListListener);
                }
                break;
            case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    manager.requestConnectionInfo(channel, activity.connectionListener);
                } else k.p2pConnected = false;
                break;
            case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                // Respond to this device's wifi state changing
                break;
        }
    }
}
