package com.pgeiser.mpremote.fragment_characteristics

import android.app.Application
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity

class CharacteristicsViewModelFactory (
    private val application: Application,
    private val activity : MainActivity,
    private val service : BluetoothGattService,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacteristicsViewModel::class.java)) {
            return CharacteristicsViewModel(application, activity, service) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}