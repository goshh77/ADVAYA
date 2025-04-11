package com.example.wifidirectdemo

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import java.io.*
import java.net.ServerSocket
import java.net.Socket

class MainActivity : ComponentActivity(), WifiP2pManager.ConnectionInfoListener {
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter()
    private val peers = mutableListOf<WifiP2pDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        receiver = WiFiDirectBroadcastReceiver(manager, channel, this, peers)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        setContent {
            var peerList by remember { mutableStateOf(listOf<WifiP2pDevice>()) }

            LaunchedEffect(Unit) {
                peerList = peers
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Button(onClick = { discoverPeers() }) {
                    Text("Discover Peers")
                }
                Spacer(modifier = Modifier.height(8.dp))
                peerList.forEach { device ->
                    Button(onClick = { connectToDevice(device) }) {
                        Text("Connect to: ${device.deviceName}")
                    }
                }
            }
        }
    }

    private fun discoverPeers() {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "Discovery Started", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Discovery Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun connectToDevice(device: WifiP2pDevice) {
        val config = WifiP2pConfig().apply { deviceAddress = device.deviceAddress }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Toast.makeText(this@MainActivity, "Connecting to ${device.deviceName}", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(this@MainActivity, "Connection Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        info?.let {
            if (it.groupFormed && it.isGroupOwner) {
                Toast.makeText(this, "Group Owner - Starting Server", Toast.LENGTH_SHORT).show()
                startServer()
            } else if (it.groupFormed) {
                val hostAddress = it.groupOwnerAddress?.hostAddress
                Toast.makeText(this, "Client - Connecting to $hostAddress", Toast.LENGTH_SHORT).show()
                startClient(hostAddress ?: "")
            }
        }
    }

    private fun startServer() {
        Thread {
            try {
                val serverSocket = ServerSocket(8888)
                val client = serverSocket.accept()
                val output = BufferedWriter(OutputStreamWriter(client.getOutputStream()))
                output.write("Hello from Host!\n")
                output.flush()
                output.close()
                client.close()
                serverSocket.close()
                runOnUiThread {
                    Toast.makeText(this, "Message sent to peer", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun startClient(host: String) {
        Thread {
            try {
                val socket = Socket(host, 8888)
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val message = input.readLine()
                input.close()
                socket.close()
                runOnUiThread {
                    Toast.makeText(this, "Message received: $message", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
}
