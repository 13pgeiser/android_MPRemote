package com.pgeiser.mpremote

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import kotlinx.coroutines.*
import timber.log.Timber

open class MyGattCallback(
    private val activity: MainActivity,
    private val bluetoothDevice : BluetoothDevice,
) : BluetoothGattCallback() {
    var callbackConnectionAttempt : Int = 0
    private val callbackmaxConnectionAttempt = 20
    private val GATT_MAX_MTU_SIZE = 517

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
            callbackConnectionAttempt ++
            if (callbackConnectionAttempt <= callbackmaxConnectionAttempt) {
                connect()
            } else {
                Timber.w("No way...")
            }
        }
    }

    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
        Timber.w("ATT MTU changed to $mtu, success: ${status == BluetoothGatt.GATT_SUCCESS}")
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gatt.discoverServices()
        } else {
            gatt.close()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        with(gatt) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.w("Discovered ${services.size} services for ${device.address}.")
            } else {
                Timber.e("Service discovery failed due to status $status")
                gatt.close()
            }
        }
    }
    private fun connect() {
        bluetoothDevice.connectGatt(activity.applicationContext, false, this)
    }
}