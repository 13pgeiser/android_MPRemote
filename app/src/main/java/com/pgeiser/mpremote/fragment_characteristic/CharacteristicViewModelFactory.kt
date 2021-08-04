package com.pgeiser.mpremote.fragment_characteristic

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity

class CharacteristicViewModelFactory (
    private val application: Application,
    private val activity : MainActivity,
    private val characteristic : BluetoothGattCharacteristic,
    private val bluetoothDevice : BluetoothDevice,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacteristicViewModel::class.java)) {
            return CharacteristicViewModel(application, activity, characteristic, bluetoothDevice) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}