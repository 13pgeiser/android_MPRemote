package com.pgeiser.mpremote.fragment_characteristic

import android.os.Bundle
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
        val viewModelFactory = CharacteristicViewModelFactory(application, activity, safeArgs.characteristic)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CharacteristicViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        Timber.i("onCreateView: done!")
        return binding.root
    }
}
