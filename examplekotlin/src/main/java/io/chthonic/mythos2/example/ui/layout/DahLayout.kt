package io.chthonic.mythos2.example.ui.layout

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.LayoutDahBinding
import io.chthonic.mythos2.mvvm.MVVMLayout
import io.chthonic.mythos2.mvvm.ViewControllerInfo
import io.chthonic.mythos2.example.ui.viewmodel.DahViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils

/**
 * Created by jhavatar on 5/30/2020.
 */
class DahLayout : MVVMLayout<DahViewModel, LayoutDahBinding> {

    override val vci: ViewControllerInfo<DahViewModel, LayoutDahBinding> by lazy {
        ViewControllerInfo.compatViewControllerSharedViewModel<DahViewModel, LayoutDahBinding>(this, R.layout.layout_dah)
    }

    override fun onCreate() {
        vci.bindViewModel<DahViewModel>(application)
        vci.bindViewData(LayoutInflater.from(context), this, true)
        vci.viewDataBinding.dahText.text = "view = ${ExampleUtils.getInstanceCount(this, this::class.java)}, viewModel = ${vci.viewModel.instanceCount}"
    }

    override fun onDestroy() {
    }

    constructor(context: Context, parentFragmentTag: String? = null, @IdRes parentFragmentId: Int? = null) : super(context, parentFragmentTag, parentFragmentId)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
}