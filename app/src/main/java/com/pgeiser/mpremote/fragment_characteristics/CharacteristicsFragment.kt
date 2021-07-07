package com.pgeiser.mpremote.fragment_characteristics

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
import com.pgeiser.mpremote.databinding.FragmentCharacteristicsBinding
import timber.log.Timber

class CharacteristicsFragment : Fragment() {
    private lateinit var viewModel: CharacteristicsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentCharacteristicsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_characteristics, container, false
        )
        val application = requireNotNull(this.activity).application
        val activity = requireActivity() as MainActivity
        val safeArgs = CharacteristicsFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = CharacteristicsViewModelFactory(application, activity, safeArgs.service)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CharacteristicsViewModel::class.java)
        binding.model = viewModel
        binding.lifecycleOwner = this
        val adapter = CharacteristicsAdapter(CharacteristicsListener {
                characteristicId -> Toast.makeText(context, "characteristicId: ${characteristicId}", Toast.LENGTH_SHORT).show()
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter.submitList( safeArgs.service.characteristics.toList())
        viewModel.gattService.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.characteristics.toList())
        })
        Timber.i("onCreateView: service=%s", safeArgs.service.toString())
        return binding.root
    }
}