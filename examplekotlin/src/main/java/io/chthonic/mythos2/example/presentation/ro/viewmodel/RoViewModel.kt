package io.chthonic.mythos2.example.presentation.ro.viewmodel

import io.chthonic.mythos2.example.presentation.common.viewmodel.ExampleViewModel
import io.chthonic.mythos2.mvvm.MythosViewModelArgs

/**
 * Created by jhavatar on 3/15/2020.
 */
class RoViewModel(baseViewModelArgs: MythosViewModelArgs) : ExampleViewModel(baseViewModelArgs) {

    override val viewModelClass: Class<*>
        get() = this::class.java

}
