package com.pgeiser.mpremote.fragment_discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.pgeiser.mpremote.MainActivity
import com.pgeiser.mpremote.R
import com.pgeiser.mpremote.databinding.FragmentDiscoverBinding
import timber.log.Timber

class DiscoverFragment : Fragment() {
    private lateinit var viewModel: DiscoverViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentDiscoverBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_discover, container, false
        )
        val application = requireNotNull(this.activity).application
        val activity = requireActivity() as MainActivity
        val safeArgs = DiscoverFragmentArgs.fromBundle(requireArguments())
        val bluetoothDevice = safeArgs.bluetoothDevice
        val viewModelFactory = ConnectViewModelFactory(application, activity, bluetoothDevice)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DiscoverViewModel::class.java)
        viewModel.gattServices.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Timber.i("ConnectFragmentDirections.actionConnectViewFragmentToAttributesFragment")
                requireView().findNavController().navigate(
                    DiscoverFragmentDirections.actionConnectViewFragmentToAttributesFragment(it))
            }
        })
        viewModel.discover()
        binding.model = viewModel
        binding.lifecycleOwner = this
        Timber.i("onCreateView: done!")
        return binding.root
    }
}