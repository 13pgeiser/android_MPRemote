package com.pgeiser.mpremote.fragment_services

import android.app.Application
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity

class ServicesViewModelFactory (
    private val application: Application,
    private val activity : MainActivity,
    private val services : Array<BluetoothGattService>,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServicesViewModel::class.java)) {
            return ServicesViewModel(application, activity, services) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}