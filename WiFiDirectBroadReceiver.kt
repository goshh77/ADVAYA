package com.example.wifidirectdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast

class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val context: Context,
    private val peers: MutableList<android.net.wifi.p2p.WifiP2pDevice>
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(channel) { peerList: WifiP2pDeviceList ->
                    peers.clear()
                    peers.addAll(peerList.deviceList)
                    Toast.makeText(context, "Peers updated: ${peers.size}", Toast.LENGTH_SHORT).show()
                }
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                if (networkInfo?.isConnected == true) {
                    manager.requestConnectionInfo(channel, context as WifiP2pManager.ConnectionInfoListener)
                }
            }
        }
    }
}
