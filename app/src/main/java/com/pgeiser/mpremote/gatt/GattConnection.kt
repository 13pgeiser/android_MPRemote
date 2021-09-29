package com.pgeiser.mpremote.gatt

import android.bluetooth.*
import android.content.Context
import timber.log.Timber

private abstract class BleOp {
    abstract fun perform(gattConnection: GattConnection)
}

private class BleOpConnect(
    private val callback : ((Int) -> Unit)?
) : BleOp() {
    override fun perform(gattConnection: GattConnection) {
        Timber.i("perform BleOpConnect $gattConnection.gatt")
        gattConnection.bluetoothDevice.connectGatt(
            gattConnection.context, false, gattConnection)
    }

    fun handled(status: Int) {
        callback?.invoke(status)
    }
}

private class BleOpMtu(
    private val mtu : Int,
    private val callback : ((Int) -> Unit)?
): BleOp() {
    override fun perform(gattConnection: GattConnection) {
        Timber.i("perform BleOpMtu $gattConnection.gatt")
        gattConnection.gatt?.requestMtu(mtu)
    }

    fun handled(status: Int) {
        callback?.invoke(status)
    }
}

private class BleOpDiscover(
    private val callback : ((Array<BluetoothGattService>) -> Unit)?
): BleOp() {
    override fun perform(gattConnection: GattConnection) {
        Timber.i("perform BleOpDiscover $gattConnection.gatt")
        gattConnection.gatt?.discoverServices()
    }

    fun handled(services: Array<BluetoothGattService>) {
        callback?.invoke(services)
    }
}

private class BleOpReadCharacteristic(
    private val characteristic: BluetoothGattCharacteristic,
    private val callback : ((ByteArray) -> Unit)?
): BleOp() {
    override fun perform(gattConnection: GattConnection) {
        Timber.i("perform BleOpReadCharacteristic $gattConnection.gatt")
        gattConnection.gatt?.readCharacteristic(characteristic)
    }

    fun handled(data : ByteArray) {
        callback?.invoke(data)
    }
}

private class BleOpWriteCharacteristic(
    private val characteristic: BluetoothGattCharacteristic,
    private val callback : ((Boolean) -> Unit)?
): BleOp() {
    override fun perform(gattConnection: GattConnection) {
        Timber.i("perform BleOpWriteCharacteristic $gattConnection.gatt")
        gattConnection.gatt?.writeCharacteristic(characteristic)
    }

    fun handled(success : Boolean) {
        callback?.invoke(success)
    }
}

class GattConnection (
    internal val context : Context,
    internal val bluetoothDevice : BluetoothDevice,
) : BluetoothGattCallback() {
    private val operations: MutableList<BleOp> = mutableListOf<BleOp>()
    private var runningOp: BleOp? = null
    internal var gatt : BluetoothGatt? = null

    @Synchronized
    private fun enqueue(op: BleOp) {
        Timber.i("enqueue $op")
        operations.add(op)
        if (runningOp == null)
            processOps()
    }

    @Synchronized
    private fun processOps() {
        if (operations.isEmpty()) {
            Timber.i("processOps Nothing to do!")
        } else {
            runningOp = operations.removeAt(0)
            Timber.i("processOps dequeued: $runningOp")
            runningOp!!.perform(this)
        }
    }

    @Synchronized
    private fun currentOpPerformed() {
        Timber.i("currentOpPerformed $runningOp")
        runningOp = null
        processOps()
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)
        val deviceAddress = gatt.device.address
        Timber.i("onConnectionStateChange $gatt $status $newState $deviceAddress")
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.gatt = gatt
        } else if (status == Gatt.GATT_ERROR) {
            this.gatt = null
        }
        val currentOp = runningOp as BleOpConnect
        currentOp.handled(status)
        currentOpPerformed()
    }

    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
        super.onMtuChanged(gatt, mtu, status)
        Timber.w("onMtuChanged status: $status, mtu: $mtu")
        val currentOp = runningOp as BleOpMtu
        currentOp.handled(status)
        currentOpPerformed()
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        Timber.w("onServicesDiscovered status: $status")
        val currentOp = runningOp as BleOpDiscover
        currentOp.handled(gatt.services.toTypedArray())
        currentOpPerformed()
    }

    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic?, status: Int) {
        super.onCharacteristicRead(gatt, characteristic, status)
        Timber.w("onCharacteristicRead status: $status")
        val currentOp = runningOp as BleOpReadCharacteristic
        currentOp.handled(characteristic!!.value)
        currentOpPerformed()
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?,
        status: Int
    ) {
        super.onCharacteristicWrite(gatt, characteristic, status)
        Timber.w("onCharacteristicRead status: $status")
        val currentOp = runningOp as BleOpWriteCharacteristic
        currentOp.handled(status == BluetoothGatt.GATT_SUCCESS)
        currentOpPerformed()
    }

    fun connect(callback : ((Int) -> Unit)?) {
        enqueue(BleOpConnect(callback))
    }

    fun mtu(mtu: Int, callback : ((Int) -> Unit)?) {
        enqueue(BleOpMtu(mtu, callback))
    }

    fun discover(callback : ((Array<BluetoothGattService>) -> Unit)?) {
        enqueue(BleOpDiscover(callback))
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic, callback : ((ByteArray) -> Unit)?) {
        enqueue(BleOpReadCharacteristic(characteristic, callback))
    }

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, callback : ((Boolean) -> Unit)?) {
        enqueue(BleOpWriteCharacteristic(characteristic, callback))
    }
}