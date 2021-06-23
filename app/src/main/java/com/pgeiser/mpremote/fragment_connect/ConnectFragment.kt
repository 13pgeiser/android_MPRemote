package com.pgeiser.mpremote.fragment_connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity
import com.pgeiser.mpremote.R
import com.pgeiser.mpremote.databinding.FragmentConnectBinding
import timber.log.Timber

class ConnectFragment : Fragment() {
    private lateinit var viewModel: ConnectViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentConnectBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_connect, container, false
        )
        val application = requireNotNull(this.activity).application
        var activity = requireActivity() as MainActivity
        val safeArgs = ConnectFragmentArgs.fromBundle(requireArguments())
        val bluetoothDevice = safeArgs.bluetoothDevice
        val viewModelFactory = ConnectViewModelFactory(application, activity, bluetoothDevice)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ConnectViewModel::class.java)
        binding.model = viewModel
        binding.lifecycleOwner = this
        Timber.i("onCreateView: done!")
        return binding.root
    }
}