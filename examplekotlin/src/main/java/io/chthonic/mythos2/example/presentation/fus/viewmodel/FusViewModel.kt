package io.chthonic.mythos2.example.presentation.fus.viewmodel

import io.chthonic.mythos2.example.presentation.common.viewmodel.ExampleViewModel
import io.chthonic.mythos2.mvvm.MythosViewModelArgs

class FusViewModel(baseViewModelArgs: MythosViewModelArgs) : ExampleViewModel(baseViewModelArgs) {

    override val viewModelClass: Class<*>
        get() = this::class.java

}
