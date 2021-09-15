package com.pgeiser.mpremote.fragment_discover

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.*
import com.pgeiser.mpremote.MainActivity
import com.pgeiser.mpremote.gatt.Gatt
import com.pgeiser.mpremote.gatt.GattConnection
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
    private val GATT_MAX_MTU_SIZE = 517


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val _connectionAttempt = MutableLiveData<Int>()
    val connectionAttempt : LiveData<Int> get() = _connectionAttempt
    private val _bluetoothDevice = MutableLiveData<BluetoothDevice>()
    val bluetoothDevice : LiveData<BluetoothDevice>  get() = _bluetoothDevice
    private val _gattServices = MutableLiveData<Array<BluetoothGattService>>()
    val gattServices : LiveData<Array<BluetoothGattService>>  get() = _gattServices

    private lateinit var gattConnection : GattConnection

    init {
        Timber.i("init: %s", btDev)
        _bluetoothDevice.value = btDev
        activity.lifecycle.addObserver(this)
    }

    private fun handleConnection(status : Int) {
        Timber.i("connect status %s", status.toString())
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gattConnection.mtu(GATT_MAX_MTU_SIZE, null)
            gattConnection.discover {
                uiScope.launch {
                    _gattServices.value = it
                }
            }
        } else {
            uiScope.launch {
                _connectionAttempt.value = _connectionAttempt.value?.plus(1)
            }
            gattConnection.connect(::handleConnection)
        }
    }

    private fun discover() {
        Timber.i("discover")
        uiScope.launch {
            _connectionAttempt.value = 0
            _gattServices.value = null
        }
        gattConnection = GattConnection(activity.applicationContext, btDev)
        activity.gattConnection = gattConnection
        gattConnection.connect(::handleConnection)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        Timber.i("onResume")
        discover()
    }
}