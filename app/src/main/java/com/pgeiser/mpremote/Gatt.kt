package com.pgeiser.mpremote

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Gatt {

    companion object {
        fun gattCharacteristicPropertiesAsString(ch: BluetoothGattCharacteristic): String {
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

        fun gattServiceAsString(service: BluetoothGattService): String {
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

        fun gattServicesAsString(services: Array<BluetoothGattService>): String {
            var serviceString: String = ""
            services.forEach() { service ->
                serviceString += gattServiceAsString(service)
            }
            return serviceString
        }

        private val serviceMap: HashMap<String, String> = hashMapOf(
            // See https://specificationrefs.bluetooth.com/assigned-values/16-bit%20UUID%20Numbers%20Document.pdf
            "00001801-0000-1000-8000-00805f9b34fb" to "Generic Attribute",
            "0000180a-0000-1000-8000-00805f9b34fb" to "Device Information",
            "5a791800-0d19-4fd9-87f9-e934aedbce59" to "Roger Service",
            "0000180f-0000-1000-8000-00805f9b34fb" to "Battery",
            "00001800-0000-1000-8000-00805f9b34fb" to "Generic Access",
        )


        fun gattUuidAsString(uuid : UUID) : String {
            val uuidAsString = uuid.toString()
            val serviceString : String = if (serviceMap.containsKey(uuidAsString)) {
                serviceMap[uuidAsString]!!
            } else {
                uuidAsString
            }
            Timber.i(serviceString)
            return serviceString
        }
    }
}