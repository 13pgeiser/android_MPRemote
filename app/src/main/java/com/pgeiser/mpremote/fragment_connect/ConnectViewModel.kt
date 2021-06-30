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
                    _connectionAttempt.value = _connectionAttempt.value?.plus(1)
                }
                if (connectionAttempt.value!! <= maxConnectionAttempt) {
                    defaultScope.launch {
                        Timber.i("Waiting...")
                        delay(250)
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

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            Timber.i("Discovered ${gatt.services.size} services for ${gatt.device.address}.")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (gatt.services.isEmpty()) {
                    Timber.i("No Services found!!!!")
                    uiScope.launch {
                        _gattServices.value = gatt.services.toTypedArray()
                    }
                } else {
                    uiScope.launch {
                        _gattServices.value = gatt.services.toTypedArray()
                    }
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
    private var maxConnectionAttempt = 10
    private val _bluetoothDevice = MutableLiveData<BluetoothDevice>()
    val bluetoothDevice : LiveData<BluetoothDevice>  get() = _bluetoothDevice
    private val _gattServices = MutableLiveData<Array<BluetoothGattService>>()
    val gattServices : LiveData<Array<BluetoothGattService>>  get() = _gattServices

    init {
        Timber.i("init: %s", btDev)
        _bluetoothDevice.value = btDev
        activity.lifecycle.addObserver(this)
    }

    fun discover() {
        Timber.i("discover")
        uiScope.launch {
            _connectionAttempt.value = 0
            _gattServices.value = null
        }
        connectGattInternal()
    }

    private fun connectGattInternal() {
        bluetoothDevice.value?.connectGatt(activity.applicationContext, false, gattCallback)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        Timber.i("onStart")
        discover()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        Timber.i("onStop")
    }
}