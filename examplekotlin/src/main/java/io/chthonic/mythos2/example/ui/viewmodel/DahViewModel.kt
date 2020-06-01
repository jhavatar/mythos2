package io.chthonic.mythos2.example.ui.viewmodel

import androidx.lifecycle.LiveData
import io.chthonic.mythos2.mvvm.MythosViewModelArgs
import io.chthonic.mythos2.mvvm.MythosViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils
import timber.log.Timber

/**
 * Created by jhavatar on 5/31/2020.
 */
class DahViewModel(baseViewModelArgs: MythosViewModelArgs) : MythosViewModel(baseViewModelArgs) {

    val liveViewModelInstanceCount: LiveData<Int>
        get() = ExampleUtils.getLiveInstanceCount(this::class.java)

    init {
        ExampleUtils.notifyInstance(this)
        Timber.d("init: args = $args, args.keySet = ${args.keySet().joinToString()}, savedState = $savedState, savedState.keys = ${savedState.keys().joinToString()}")
    }
}