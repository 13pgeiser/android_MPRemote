package com.pgeiser.mpremote.fragment_attributes

import android.app.Application
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity

class AttributesViewModelFactory (
    private val application: Application,
    private val activity : MainActivity,
    private val services : Array<BluetoothGattService>,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttributesViewModel::class.java)) {
            return AttributesViewModel(application, activity, services) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}