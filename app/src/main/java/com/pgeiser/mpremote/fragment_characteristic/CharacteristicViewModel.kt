package com.pgeiser.mpremote.fragment_characteristic

import android.app.Application
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.*
import com.pgeiser.mpremote.Gatt
import com.pgeiser.mpremote.MainActivity
import timber.log.Timber

class CharacteristicViewModel(
    application: Application,
    private val activity: MainActivity,
    private val init_characteristic : BluetoothGattCharacteristic,
)
    : AndroidViewModel(application) , LifecycleObserver {

    private val _characteristic = MutableLiveData<BluetoothGattCharacteristic>()
    val characteristic : LiveData<BluetoothGattCharacteristic>  get() = _characteristic
    val characteristicString = Transformations.map(characteristic) {
            characteristic-> Gatt.gattUuidAsString(characteristic.uuid)
    }

    init {
        Timber.i("init: %s", init_characteristic)
        _characteristic.value = init_characteristic
    }
}