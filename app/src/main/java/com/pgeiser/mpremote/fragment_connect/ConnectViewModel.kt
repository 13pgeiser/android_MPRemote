package com.pgeiser.mpremote.fragment_connect

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import com.pgeiser.mpremote.MainActivity

class ConnectViewModel(
    application: Application,
    private val activity: MainActivity,
    val bluetoothDevice: BluetoothDevice,
    )
    : AndroidViewModel(application) {
}