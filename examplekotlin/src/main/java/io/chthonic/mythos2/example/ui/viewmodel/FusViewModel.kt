package io.chthonic.mythos2.example.ui.viewmodel

import io.chthonic.mythos2.mvvm.MythosViewModelArgs
import io.chthonic.mythos2.mvvm.MythosViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils
import timber.log.Timber

class FusViewModel(baseViewModelArgs: MythosViewModelArgs) : MythosViewModel(baseViewModelArgs) {

    val instanceCount: Int
        get() = ExampleUtils.getInstanceCount(this, this::class.java)

    init {
        Timber.d("init: args = $args, args.keySet = ${args.keySet().joinToString()}, savedState = $savedState, savedState.keys = ${savedState.keys().joinToString()}")
    }
}
