package io.chthonic.mythos2.example.presentation.ro.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.MythosViewModel
import io.chthonic.mythos2.mvvm.MythosViewModelArgs
import timber.log.Timber

/**
 * Created by jhavatar on 3/15/2020.
 */
class RoViewModel(baseViewModelArgs: MythosViewModelArgs) : MythosViewModel(baseViewModelArgs) {

    val liveViewModelInstanceCount: LiveData<String>
        get() = Transformations.map(ExampleUtils.getLiveInstanceCount(this::class.java)) {
            it.toString()
        }

    init {
        ExampleUtils.notifyInstance(this)
        Timber.v(
            "init: args = $args, " +
                "args.keySet = ${args.keySet().joinToString()}, " +
                "savedState = $savedState, " +
                "savedState.keys = ${savedState.keys().joinToString()}"
        )
    }

}
