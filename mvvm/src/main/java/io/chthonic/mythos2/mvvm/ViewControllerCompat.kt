package io.chthonic.mythos2.mvvm

import androidx.lifecycle.LifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner

/**
 * Created by jhavatar on 5/30/2020.
 */
interface ViewControllerCompat {
    val savedStateOwner: SavedStateRegistryOwner
    val lifeCycleOwner: LifecycleOwner
}
