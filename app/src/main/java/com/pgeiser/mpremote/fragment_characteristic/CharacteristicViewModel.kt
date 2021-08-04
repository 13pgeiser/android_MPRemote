package com.pgeiser.mpremote.fragment_characteristic

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.*
import com.pgeiser.mpremote.Gatt
import com.pgeiser.mpremote.MainActivity
import com.pgeiser.mpremote.MyGattCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.util.*

class CharacteristicViewModel(
    application: Application,
    private val activity: MainActivity,
    private val init_characteristic : BluetoothGattCharacteristic,
    private val bluetoothDevice : BluetoothDevice,

    )
    : AndroidViewModel(application) , LifecycleObserver {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val defaultScope = CoroutineScope(Dispatchers.Default + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _characteristic = MutableLiveData<BluetoothGattCharacteristic>()
    val characteristic : LiveData<BluetoothGattCharacteristic>  get() = _characteristic
    val characteristicString = Transformations.map(characteristic) {
            characteristic -> Gatt.gattUuidAsString(characteristic.uuid)
    }

    val characteristicPropertiesString = Transformations.map(characteristic) {
        characteristic -> Gatt.gattCharacteristicPropertiesAsString(characteristic)
    }

    private val _characteristicValue = MutableLiveData<String>()
    val characteristicValue : LiveData<String>  get() = _characteristicValue

    init {
        activity.lifecycle.addObserver(this)
    }

    private fun setCharacteristic(c : BluetoothGattCharacteristic) {
        _characteristic.value = c
        if ((c.properties and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
            val readCallback = object : MyGattCallback(activity, bluetoothDevice) {
                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    super.onServicesDiscovered(gatt, status)
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        gatt.readCharacteristic(characteristic.value!!)
                    }
                }

                override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?, status: Int) {
                    super.onCharacteristicRead(gatt, characteristic, status)
                    uiScope.launch {
                        val data = characteristic!!.value
                        if (data.size == 1) {
                            _characteristicValue.value = data.first().toInt().toString()
                        } else {
                            _characteristicValue.value = Gatt.getStringForByteArray(data)
                        }
                    }
                    gatt.close()
                }
            }
            bluetoothDevice.connectGatt(activity.applicationContext, false, readCallback)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        Timber.i("onResume")
        setCharacteristic(init_characteristic)
    }
}