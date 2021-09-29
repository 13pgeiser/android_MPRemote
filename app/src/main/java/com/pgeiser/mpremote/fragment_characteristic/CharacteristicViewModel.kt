package com.pgeiser.mpremote.fragment_characteristic

import android.app.AlertDialog
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText
import androidx.lifecycle.*
import com.pgeiser.mpremote.gatt.Gatt
import com.pgeiser.mpremote.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

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

    fun parseString(orig_text : String) : ByteArray?{
        var text = orig_text
        if (text[0] == '[') {
            if (text[text.length-1] != ']') {
                return null
            }
            text = text.substring(1, text.length-1)
        }
        var result: List<String> = text.split(",").map { it.trim() }
        var data = ByteArray(0)
        result.forEach {
            var value : Int
            if (it.startsWith("0x")) {
                value = Integer.parseInt(it.substring(2), 16)
            } else {
                value = Integer.parseInt(it)
            }
            if ( (value < 0) || (value > 255) ) {
                return null
            }
            data += value.toByte()
        }
        return data
    }

    fun showDialog(value : String?){
        val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle("Please enter value to write")
        val input = EditText(activity)
        input.hint = "Enter Text"
        Timber.i("showDialog! value = *%s*", value)
        if (value != null) {
            input.setText(value)
        } else {
            input.setText("[4,1]")
        }
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
            var text = input.text.toString()
            var bytes = parseString(text)
            Timber.i("showDialog! %s", text)
            if (bytes != null) {
                Timber.i("showDialog! bytes %s", bytes.toString())
                bytes.forEach {
                    Timber.i(it.toString())
                }
                val gattConnection = activity.gattConnection
                _characteristic.value!!.value = bytes
                gattConnection?.writeCharacteristic(characteristic.value!!) {
                    Timber.i("Done: %s", it.toString())
                }
            }

        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

        builder.show()
    }

    fun onClick() {
        Timber.i("onClick!")
        if ( (characteristic.value!!.properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0)
            || (characteristic.value!!.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0)) {
            showDialog(_characteristicValue.value)
        }
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    fun readCharacteristic(): Boolean {
        _characteristic.value = init_characteristic
        return if (characteristic.value!!.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
            val gattConnection = activity.gattConnection
            gattConnection?.readCharacteristic(characteristic.value!!) {
                uiScope.launch {
                    if (it.size == 1) {
                        _characteristicValue.value = it.first().toInt().toString()
                    } else {
                        _characteristicValue.value = Gatt.getStringForByteArray(it)
                    }
                }
            }
            true
        } else {
            false
        }
    }
}