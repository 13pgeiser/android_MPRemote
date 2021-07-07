package com.pgeiser.mpremote.fragment_characteristics

import android.app.Application
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.*
import com.pgeiser.mpremote.MainActivity
import timber.log.Timber

class CharacteristicsViewModel(
    application: Application,
    private val activity: MainActivity,
    private val service : BluetoothGattService,
)
    : AndroidViewModel(application) , LifecycleObserver {

    private val _gattService = MutableLiveData<BluetoothGattService>()
    val gattService : LiveData<BluetoothGattService> get() = _gattService
    init {
        Timber.i("init: %s", service)
        activity.lifecycle.addObserver(this)
        _gattService.value = service
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        Timber.i("onStart")
        _gattService.value = null
        _gattService.value = service
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        Timber.i("onStop")
    }

}