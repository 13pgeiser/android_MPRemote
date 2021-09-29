package com.pgeiser.mpremote.fragment_characteristic

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity
import com.pgeiser.mpremote.R
import com.pgeiser.mpremote.databinding.FragmentCharacteristicBinding
import timber.log.Timber


class CharacteristicFragment : Fragment() {
    private lateinit var viewModel: CharacteristicViewModel
    private lateinit var handler : Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentCharacteristicBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_characteristic, container, false
        )
        val application = requireNotNull(this.activity).application
        val activity = requireActivity() as MainActivity
        val safeArgs = CharacteristicFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = CharacteristicViewModelFactory(application, activity, safeArgs.characteristic, safeArgs.bluetoothDevice)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CharacteristicViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        Timber.i("onCreateView: done!")
        handler = Handler(Looper.getMainLooper())
        return binding.root
    }

    private fun refreshUI() {
        val r: Runnable = object : Runnable {
            override fun run() {
                viewModel.readCharacteristic()
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(r, 1000)
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")
        if (viewModel.readCharacteristic()) {
            refreshUI()
        }
    }

    override fun onPause() {
        handler.removeCallbacksAndMessages(null)
        super.onPause()
        Timber.i("onPause")
    }
}
