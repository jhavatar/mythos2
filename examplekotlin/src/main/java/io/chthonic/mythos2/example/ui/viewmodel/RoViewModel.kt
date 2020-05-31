package io.chthonic.mythos2.example.ui.viewmodel

import io.chthonic.mythos2.mvvm.MythosViewModelArgs
import io.chthonic.mythos2.mvvm.MythosViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils
import timber.log.Timber

/**
 * Created by jhavatar on 3/15/2020.
 */
class RoViewModel(baseViewModelArgs: MythosViewModelArgs) : MythosViewModel(baseViewModelArgs) {

    val instanceCount: Int
        get() = ExampleUtils.getInstanceCount(this, this::class.java)

    init {
        Timber.d("init: args = $args, args.keySet = ${args.keySet().joinToString()}, savedState = $savedState, savedState.keys = ${savedState.keys().joinToString()}")
    }
}