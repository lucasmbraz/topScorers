package com.lucasmbraz.topscorers.topscorers

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.lucasmbraz.topscorers.Injection

class TopScorersViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == TopScorersViewModel::class.java) {
            val actionProcessor = TopScorersActionProcessorHolder(Injection.provideTopScorersRepository())
            return TopScorersViewModel(actionProcessor) as T
        }
        return super.create(modelClass)
    }
}