package com.pgeiser.mpremote.fragment_characteristics

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pgeiser.mpremote.gatt.Gatt
import com.pgeiser.mpremote.databinding.ListItemCharacteristicBinding
import timber.log.Timber

class CharacteristicsAdapter(private val clickListener: CharacteristicsListener) : ListAdapter<BluetoothGattCharacteristic, CharacteristicsAdapter.ViewHolder>(CharacteristicsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.i("onCreateViewHolder!")

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.i("onBindViewHolder!")
        holder.bind(clickListener, getItem(position))
    }

    class ViewHolder private constructor(private val binding: ListItemCharacteristicBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: CharacteristicsListener, characteristic: BluetoothGattCharacteristic) {
            Timber.i("bind! characteristic.uuid=%s", characteristic.uuid.toString())
            binding.characteristic = characteristic
            binding.gatt = Gatt.Companion
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                Timber.i("from!")
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemCharacteristicBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CharacteristicsDiffCallback :
    DiffUtil.ItemCallback<BluetoothGattCharacteristic>() {
    override fun areItemsTheSame(oldItem: BluetoothGattCharacteristic, newItem: BluetoothGattCharacteristic): Boolean {
        Timber.i("areItemsTheSame!")
        return oldItem.instanceId == newItem.instanceId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: BluetoothGattCharacteristic, newItem: BluetoothGattCharacteristic): Boolean {
        Timber.i("areContentsTheSame!")
        return oldItem == newItem
    }
}

class CharacteristicsListener(val clickListener: (instance_id: Int) -> Unit) {
    fun onClick(characteristic: BluetoothGattCharacteristic) = clickListener(characteristic.instanceId)
}