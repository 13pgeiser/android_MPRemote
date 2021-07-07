package com.pgeiser.mpremote.fragment_services

import android.bluetooth.BluetoothGattService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pgeiser.mpremote.MainActivity
import com.pgeiser.mpremote.R
import com.pgeiser.mpremote.databinding.FragmentServicesBinding
import timber.log.Timber

class ServicesFragment : Fragment() {
    private lateinit var viewModel: ServicesViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentServicesBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_services, container, false
        )
        val application = requireNotNull(this.activity).application
        val activity = requireActivity() as MainActivity
        val safeArgs = ServicesFragmentArgs.fromBundle(requireArguments())
        val services : Array<BluetoothGattService> = safeArgs.services
        val viewModelFactory = ServicesViewModelFactory(application, activity, services)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ServicesViewModel::class.java)
        binding.model = viewModel
        binding.lifecycleOwner = this
        val adapter = ServiceAdapter(ServiceListener {
                serviceId -> Toast.makeText(context, "Service: ${serviceId}", Toast.LENGTH_SHORT).show()
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter.submitList(services.toList())
        viewModel.gattServices.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.toList())
        })
        Timber.i("onCreateView: service count=%d", services.size)
        return binding.root
    }
}