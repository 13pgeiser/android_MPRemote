package com.pgeiser.mpremote.fragment_connect

import android.app.Application
import android.bluetooth.*
import androidx.lifecycle.*
import com.pgeiser.mpremote.MainActivity
import kotlinx.coroutines.*
import timber.log.Timber

class ConnectViewModel(
    application: Application,
    private val activity: MainActivity,
    private val btDev: BluetoothDevice,
    )
    : AndroidViewModel(application) , LifecycleObserver {

    // ---------------------------------------------------------------------------------------------

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val defaultScope = CoroutineScope(Dispatchers.Default + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val gattCallback = object : BluetoothGattCallback() {
        var GATT_MAX_MTU_SIZE = 517
        // ctrl-o onConnec...
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val deviceAddress = gatt.device.address
            Timber.i("onConnectionStateChange $gatt $status $newState $deviceAddress")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Timber.i("Successfully connected to $deviceAddress")
                    gatt.requestMtu(GATT_MAX_MTU_SIZE)
                } else {
                    gatt.close()
                }
            } else {
                gatt.close()
                uiScope.launch {
                    _connectionAttempt?.value = _connectionAttempt.value?.plus(1)
                }
                if (connectionAttempt.value!! <= maxConnectionAttempt) {
                    defaultScope.launch {
                        Timber.i("Waiting...")
                        delay(500)
                        connectGattInternal()
                    }
                } else {
                    Timber.w("No way...")
                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Timber.i("ATT MTU changed to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.discoverServices()
            } else {
                gatt.close()
            }
        }

        private fun printProperties(ch: BluetoothGattCharacteristic): String {
            var props: ArrayList<String> = ArrayList()
            var properties = ch.properties
            if ((properties and BluetoothGattCharacteristic.PROPERTY_READ) != 0)
                props.add("READABLE")
            if ((properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0)
                props.add("WRITABLE")
            if ((properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0)
                props.add("WRITABLE WITHOUT RESPONSE")
            if ((properties and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0)
                props.add("INDICATABLE")
            if ((properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0)
                props.add("NOTIFIABLE")
            return props.joinToString()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Timber.i("Discovered ${gatt.services.size} services for ${gatt.device.address}.")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                var serviceStr : String = ""
                if (gatt.services.isEmpty()) {
                    serviceStr = "No Services found!!!!"
                } else {
                    gatt.services.forEach() { service ->
                        val characteristicsTable = service.characteristics.joinToString(
                            separator = "\n|--",
                            prefix = "|--"
                        ) { char ->
                            var description = "${char.uuid}: ${printProperties(char)}"
                            if (char.descriptors.isNotEmpty()) {
                                description += "\n" + char.descriptors.joinToString(
                                    separator = "\n|------",
                                    prefix = "|------"
                                ) { descriptor ->
                                    "${descriptor.uuid}: ${descriptor.toString()}"
                                }
                            }
                            description
                        }
                        serviceStr += "\nService ${service.uuid}\n$characteristicsTable"
                    }
                }
                Timber.i(serviceStr)
                uiScope.launch {
                    _serviceString.value = serviceStr
                }
                gatt.close()
            } else {
                gatt.close()
            }
        }
    }
    // ---------------------------------------------------------------------------------------------

    private val _connectionAttempt = MutableLiveData<Int>()
    val connectionAttempt : LiveData<Int> get() = _connectionAttempt
    private var maxConnectionAttempt = 5
    private val _bluetoothDevice = MutableLiveData<BluetoothDevice>()
    val bluetoothDevice : LiveData<BluetoothDevice>  get() = _bluetoothDevice
    private val _serviceString = MutableLiveData<String>()
    val serviceString : LiveData<String>  get() = _serviceString

    init {
        Timber.i("init: %s", btDev)
        _bluetoothDevice.value = btDev
        connect()
        activity.lifecycle.addObserver(this)
    }

    private fun connectGattInternal() {
        bluetoothDevice.value?.connectGatt(activity.applicationContext, false, gattCallback)
    }

    private fun connect() {
        Timber.i("connect")
        uiScope.launch {
            _connectionAttempt.value = 0
        }
        connectGattInternal()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        Timber.i("onStart")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        Timber.i("onStop")
    }
}