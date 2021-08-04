package com.pgeiser.mpremote.fragment_discover

import android.app.Application
import android.bluetooth.*
import androidx.lifecycle.*
import com.pgeiser.mpremote.Gatt
import com.pgeiser.mpremote.MainActivity
import com.pgeiser.mpremote.MyGattCallback
import kotlinx.coroutines.*
import timber.log.Timber

class DiscoverViewModel(
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
        val gattCallback = object : MyGattCallback(activity, btDev) {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                uiScope.launch {
                    _connectionAttempt.value = callbackConnectionAttempt
                }
            }
            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                super.onServicesDiscovered(gatt, status)
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
                }
            }
        }
        bluetoothDevice.value?.connectGatt(activity.applicationContext, false, gattCallback)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        Timber.i("onResume")
        discover()
    }
}