package io.chthonic.mythos2.example.presentation.dah.viewmodel

import io.chthonic.mythos2.example.presentation.common.viewmodel.ExampleViewModel
import io.chthonic.mythos2.mvvm.MythosViewModelArgs

/**
 * Created by jhavatar on 5/31/2020.
 */
class DahViewModel(baseViewModelArgs: MythosViewModelArgs) : ExampleViewModel(baseViewModelArgs) {

    override val viewModelClass: Class<*>
        get() = this::class.java

}
