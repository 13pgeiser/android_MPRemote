package com.pgeiser.mpremote.fragment_discover

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity

class ConnectViewModelFactory (
    private val application: Application,
    private val activity : MainActivity,
    private val bluetoothDevice: BluetoothDevice,
    ) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiscoverViewModel::class.java)) {
            return DiscoverViewModel(application, activity, bluetoothDevice) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}