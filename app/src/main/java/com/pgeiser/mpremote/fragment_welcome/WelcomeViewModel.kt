package com.pgeiser.mpremote.fragment_welcome

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.*
import android.os.ParcelUuid
import android.widget.Toast
import androidx.lifecycle.*
import com.pgeiser.mpremote.MainActivity
import timber.log.Timber

class WelcomeViewModel(
    application: Application,
    private val activity: MainActivity,
)
    : AndroidViewModel(application), LifecycleObserver {

    var uuid : String = "5a791800-0d19-4fd9-87f9-e934aedbce59"
    private val _bluetoothIsScanning = MutableLiveData<Boolean>()
    val bluetoothIsScanning : LiveData<Boolean> get() = _bluetoothIsScanning
    val bluetoothIsScanningString: LiveData<String> =  Transformations.map(bluetoothIsScanning) {
        scan -> when(scan) {
            true -> "Stop Scan"
            false ->"Start Scan"
        }
    }
    private val _bluetoothDevice = MutableLiveData<BluetoothDevice>()
    val bluetoothDevice : LiveData<BluetoothDevice>  get() = _bluetoothDevice

    private val bluetoothLeScanner: BluetoothLeScanner by lazy {
        if (activity.bluetoothAdapter.bluetoothLeScanner == null) {
            Toast.makeText(activity, "Bluetooth must be enabled", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
        activity.bluetoothAdapter.bluetoothLeScanner
    }

    init {
        _bluetoothIsScanning.value = false
        _bluetoothDevice.value = null
        Timber.i("init")
        activity.lifecycle.addObserver(this)
    }

    fun toggleScan() {
        Timber.i("toggleScan")
        if (bluetoothIsScanning.value == true) {
            stopScanning()
        } else {
            startScanning()
        }
    }

    private fun startScanning() {
        if (bluetoothIsScanning.value == true)
            return
        Timber.i("startScanning")
        _bluetoothIsScanning.value = true
        _bluetoothDevice.value = null
        val filter = ScanFilter.Builder().setServiceUuid(
            ParcelUuid.fromString(uuid))
            .build()
        val filterList = arrayListOf(filter)
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        bluetoothLeScanner.startScan(filterList, settings, leScanCallback)
    }

    private fun stopScanning() {
        if (bluetoothIsScanning.value == false)
            return
        Timber.i("stopScanning")
        bluetoothLeScanner.stopScan(leScanCallback)
        _bluetoothIsScanning.value = false
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Timber.i("Scan result: " + result.toString())
            stopScanning()
            _bluetoothDevice.value = result.device
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        Timber.i("onStart")
        if (_bluetoothDevice.value == null) {
            startScanning()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        Timber.i("onStop")
        stopScanning()
    }
}