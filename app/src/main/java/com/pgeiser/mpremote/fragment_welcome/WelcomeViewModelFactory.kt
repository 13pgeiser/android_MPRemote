package com.pgeiser.mpremote.fragment_welcome

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pgeiser.mpremote.MainActivity

class WelcomeViewModelFactory(
    private val application: Application,
    private val activity : MainActivity,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            return WelcomeViewModel(application, activity) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}