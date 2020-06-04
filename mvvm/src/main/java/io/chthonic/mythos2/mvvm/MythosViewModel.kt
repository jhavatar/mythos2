package io.chthonic.mythos2.mvvm

import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

/**
 * Created by jhavatar on 3/15/2020.
 */
open class MythosViewModel(baseArgs: MythosViewModelArgs) : AndroidViewModel(baseArgs.application) {

    val args: Bundle = baseArgs.args
    val savedState: SavedStateHandle = baseArgs.savedState

    /**
     * [CoroutineScope] tied to this [ViewModel].
     * This scope will be canceled when ViewModel will be cleared, i.e [ViewModel.onCleared] is called
     *
     * This scope is bound to
     * [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
     */
    val coroutineScope: CoroutineScope
        get() = viewModelScope
}
