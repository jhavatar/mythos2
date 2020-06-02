package io.chthonic.mythos2.mvvm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Created by jhavatar on 5/30/2020.
 */
interface ViewControllerCompat {
    val savedStateOwner : SavedStateRegistryOwner
    val lifeCycleOwner : LifecycleOwner
}