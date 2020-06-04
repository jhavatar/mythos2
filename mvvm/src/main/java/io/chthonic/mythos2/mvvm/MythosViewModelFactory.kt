package io.chthonic.mythos2.mvvm

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Created by jhavatar on 5/24/2020.
 */
class MythosViewModelFactory(
    private val application: Application,
    owner: SavedStateRegistryOwner,
    fallbackSavedSate: Bundle? = null,
    private val args: Bundle = Bundle()
) : AbstractSavedStateViewModelFactory(owner, fallbackSavedSate) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return modelClass.getConstructor(MythosViewModelArgs::class.java).newInstance(MythosViewModelArgs(application, handle, args))
    }
}
