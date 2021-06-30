package com.pgeiser.mpremote.fragment_welcome

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
import com.pgeiser.mpremote.databinding.FragmentWelcomeBinding
import timber.log.Timber

class WelcomeFragment : Fragment() {
    private lateinit var viewModel: WelcomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentWelcomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_welcome, container, false
        )
        val application = requireNotNull(this.activity).application
        val activity = requireActivity() as MainActivity
        val viewModelFactory = WelcomeViewModelFactory(application, activity)
        viewModel = ViewModelProvider(this, viewModelFactory).get(WelcomeViewModel::class.java)
        binding.model = viewModel
        binding.lifecycleOwner = this
        viewModel.bluetoothDevice.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Timber.i("WelcomeFragmentDirections.actionWelcomeFragmentToConnectViewFragment")
                requireView().findNavController().navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToConnectViewFragment(it))
            }
        })
        Timber.i("onCreateView: done!")
        return binding.root
    }
}