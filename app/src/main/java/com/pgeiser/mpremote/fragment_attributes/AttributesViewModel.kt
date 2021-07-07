package com.pgeiser.mpremote.fragment_attributes

import android.app.Application
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.*
import com.pgeiser.mpremote.Gatt
import com.pgeiser.mpremote.MainActivity
import timber.log.Timber

class AttributesViewModel(
    application: Application,
    private val activity: MainActivity,
    private val services : Array<BluetoothGattService>,
    )
    : AndroidViewModel(application) , LifecycleObserver {

    private val _gattServices = MutableLiveData<Array<BluetoothGattService>>()
    val gattServices : LiveData<Array<BluetoothGattService>> get() = _gattServices

    private val _serviceString = MutableLiveData<String>()
    val serviceString : LiveData<String>  get() = _serviceString

    init {
        Timber.i("init: %s", services)
        activity.lifecycle.addObserver(this)
        _gattServices.value = services
        _serviceString.value = Gatt.gattServicesAsString(services)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        Timber.i("onStart")
        _serviceString.value = Gatt.gattServicesAsString(services)
        _gattServices.value = null
        _gattServices.value = services
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        Timber.i("onStop")
        _serviceString.value = ""
    }
}