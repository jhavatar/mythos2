package io.chthonic.mythos2.example.ui.layout

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.LayoutDahBinding
import io.chthonic.mythos2.example.ui.viewmodel.DahViewModel
import io.chthonic.mythos2.example.ui.vu.ExampleVu
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.MythosLayout
import io.chthonic.mythos2.mvvm.ViewControllerCore
import timber.log.Timber

/**
 * Created by jhavatar on 5/30/2020.
 */
class DahLayout : MythosLayout<DahViewModel, LayoutDahBinding, ExampleVu<LayoutDahBinding>> {

    override val vci: ViewControllerCore<DahViewModel, LayoutDahBinding, ExampleVu<LayoutDahBinding>> by lazy {
        ViewControllerCore.compatViewController<DahViewModel, LayoutDahBinding, ExampleVu<LayoutDahBinding>>(this, defaultViewModelStore)
    }

    private val liveViewCount: LiveData<Int> by lazy {
        ExampleUtils.getLiveInstanceCount(DahLayout::class.java)
    }

    override fun onCreate() {
        Timber.v("onCreate")
        vci.bindViewModel<DahViewModel>(checkNotNull(application))
        vci.bindViewData(
            LayoutInflater.from(context),
            R.layout.layout_dah,
            this,
            true,
            ::ExampleVu)
        vci.viewDataBinding.viewmodel = vci.viewModel

        liveViewCount.observe(vci.lifeCycleOwner, Observer {
            vci.vu.upateText(it)
        })
        ExampleUtils.notifyInstance(this)
    }

    override fun onStart() {
        Timber.v("onStart")
    }

    override fun onResume() {
        Timber.v("onResume")
    }

    override fun onPause() {
        Timber.v("onPause")
    }

    override fun onStop() {
        Timber.v("onStop")
    }

    override fun onDestroy() {
        Timber.v("onDestroy")
    }

    constructor(context: Context, parentFragmentTag: String? = null, @IdRes parentFragmentId: Int? = null) : super(context, parentFragmentTag, parentFragmentId)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
}
