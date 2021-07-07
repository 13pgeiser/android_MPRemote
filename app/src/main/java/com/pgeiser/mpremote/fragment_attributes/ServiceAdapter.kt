package com.pgeiser.mpremote.fragment_attributes

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattService
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pgeiser.mpremote.Gatt
import com.pgeiser.mpremote.R
import com.pgeiser.mpremote.databinding.ListItemServiceBinding
import timber.log.Timber

class ServiceAdapter(private val clickListener: ServiceListener) : ListAdapter<BluetoothGattService, ServiceAdapter.ViewHolder>(ServiceDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Timber.i("onCreateViewHolder!")

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Timber.i("onBindViewHolder!")
        holder.bind(clickListener, getItem(position))
    }

    class ViewHolder private constructor(private val binding: ListItemServiceBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ServiceListener, service: BluetoothGattService) {
            Timber.i("bind! service.uuid=%s", service.uuid.toString())
            binding.service = service
            binding.gatt = Gatt.Companion
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                Timber.i("from!")
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemServiceBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ServiceDiffCallback :
    DiffUtil.ItemCallback<BluetoothGattService>() {
    override fun areItemsTheSame(oldItem: BluetoothGattService, newItem: BluetoothGattService): Boolean {
        Timber.i("areItemsTheSame!")

        return oldItem.instanceId == newItem.instanceId
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: BluetoothGattService, newItem: BluetoothGattService): Boolean {
        Timber.i("areContentsTheSame!")
        return oldItem == newItem
    }
}

class ServiceListener(val clickListener: (instance_id: Int) -> Unit) {
    fun onClick(service: BluetoothGattService) = clickListener(service.instanceId)
}