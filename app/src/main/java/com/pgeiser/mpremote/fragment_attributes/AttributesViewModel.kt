package com.pgeiser.mpremote.fragment_attributes

import android.app.Application
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.lifecycle.*
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
        _serviceString.value = gattServicesAsString(services)
    }

    private fun gattCharacteristicPropertiesAsString(ch: BluetoothGattCharacteristic): String {
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

    private fun gattServiceAsString(service : BluetoothGattService): String {
        val characteristicsTable = service.characteristics.joinToString(
            separator = "\n|--",
            prefix = "|--"
        ) { char ->
            var description = "${char.uuid}: ${gattCharacteristicPropertiesAsString(char)}"
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
        return "\nService ${service.uuid}\n$characteristicsTable"
    }

    private fun gattServicesAsString(services : Array<BluetoothGattService>): String {
        var serviceString : String = ""
        services.forEach() { service ->
            serviceString += gattServiceAsString(service)
        }
        return serviceString
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        Timber.i("onStart")
        _serviceString.value = gattServicesAsString(services)
        _gattServices.value = null
        _gattServices.value = services
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        Timber.i("onStop")
        _serviceString.value = ""
    }
}