package io.chthonic.mythos2.example.presentation.fus.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.MythosViewModel
import io.chthonic.mythos2.mvvm.MythosViewModelArgs
import timber.log.Timber

class FusViewModel(baseViewModelArgs: MythosViewModelArgs) : MythosViewModel(baseViewModelArgs) {

    val liveViewModelInstanceCount: LiveData<String>
        get() = Transformations.map(ExampleUtils.getLiveInstanceCount(this::class.java)) {
            it.toString()
        }

    init {
        ExampleUtils.notifyInstance(this)
        Timber.d("init: args = $args, args.keySet = ${args.keySet().joinToString()}, savedState = $savedState, savedState.keys = ${savedState.keys().joinToString()}")
    }

}
