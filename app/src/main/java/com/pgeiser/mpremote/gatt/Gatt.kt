package com.pgeiser.mpremote.gatt

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
            "00001800-0000-1000-8000-00805f9b34fb" to "Generic Access",
            "00001801-0000-1000-8000-00805f9b34fb" to "Generic Attribute",
            "0000180a-0000-1000-8000-00805f9b34fb" to "Device Information",
            "0000180f-0000-1000-8000-00805f9b34fb" to "Battery",
            "00002a00-0000-1000-8000-00805f9b34fb" to "Device Name",
            "00002a01-0000-1000-8000-00805f9b34fb" to "Appearance",
            "00002a04-0000-1000-8000-00805f9b34fb" to "Peripheral Preferred Connection Parameters",
            "00002a19-0000-1000-8000-00805f9b34fb" to "Battery Level",
            "00002a24-0000-1000-8000-00805f9b34fb" to "Model Number String",
            "00002a25-0000-1000-8000-00805f9b34fb" to "Serial Number String",
            "00002a26-0000-1000-8000-00805f9b34fb" to "Firmware Revision String",
            "00002a27-0000-1000-8000-00805f9b34fb" to "Hardware Revision String",
            "00002a28-0000-1000-8000-00805f9b34fb" to "Software Revision String",
            "00002a29-0000-1000-8000-00805f9b34fb" to "Manufacturer Name String",
            "00002aa6-0000-1000-8000-00805f9b34fb" to "Central Address Resolution",
            "5a791800-0d19-4fd9-87f9-e934aedbce59" to "Roger Service",
            "5a792000-0d19-4fd9-87f9-e934aedbce59" to "Roger State",
            "5a792001-0d19-4fd9-87f9-e934aedbce59" to "Roger Control Point",
            "5a792002-0d19-4fd9-87f9-e934aedbce59" to "Roger Feature Bitmap",
            )


        fun gattUuidAsString(uuid: UUID): String {
            var uuidAsString = uuid.toString()
            Timber.i("gattUuidAsString: %s", uuidAsString)
            if (serviceMap.containsKey(uuidAsString)) {
                uuidAsString = serviceMap[uuidAsString]!!
            }
            Timber.i("gattUuidAsString: --> %s", uuidAsString)
            return uuidAsString
        }

        fun getStringForByteArray(data : ByteArray) : String {
            var decodedString = String(data, Charsets.UTF_8)
            decodedString = decodedString.replace(Regex("[^\\x00-\\x7F]"), "");
            decodedString = decodedString.replace(Regex("[\\p{C}]"), "");
            decodedString = decodedString.replace(Regex("[\\p{Cntrl}\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]"), "");
            val outputBytes: ByteArray = decodedString.toByteArray(Charsets.UTF_8)
            return if (outputBytes.contentEquals(data)) {
                decodedString
            } else {
                data.contentToString()
            }
        }

        val GATT_ERROR = 0x85
    }
}