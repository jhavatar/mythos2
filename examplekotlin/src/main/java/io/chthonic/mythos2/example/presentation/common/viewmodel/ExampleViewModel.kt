package io.chthonic.mythos2.example.presentation.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.MythosViewModel
import io.chthonic.mythos2.mvvm.MythosViewModelArgs
import timber.log.Timber

abstract class ExampleViewModel(baseViewModelArgs: MythosViewModelArgs) : MythosViewModel(baseViewModelArgs) {

    val viewModelInstanceCountObservable: LiveData<String>
        get() = Transformations.map(ExampleUtils.getInstanceCountObservable(viewModelClass)) {
            it.toString()
        }

    protected abstract val viewModelClass: Class<*>

    var viewInstanceCountObservable: LiveData<Int>? = null

    fun getViewInstanceCountObservable(viewClass: Class<*>): LiveData<Int> {
        return viewInstanceCountObservable ?: ExampleUtils.getInstanceCountObservable(viewClass).apply {
            viewInstanceCountObservable = this
        }
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
