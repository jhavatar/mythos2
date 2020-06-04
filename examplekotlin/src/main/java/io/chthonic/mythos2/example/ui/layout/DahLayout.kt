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
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.MVVMLayout
import io.chthonic.mythos2.mvvm.ViewControllerCore
import timber.log.Timber

/**
 * Created by jhavatar on 5/30/2020.
 */
class DahLayout : MVVMLayout<DahViewModel, LayoutDahBinding> {

    override val vci: ViewControllerCore<DahViewModel, LayoutDahBinding> by lazy {
        ViewControllerCore.compatViewController<DahViewModel, LayoutDahBinding>(this, R.layout.layout_dah, defaultViewModelStore)
    }

    private val liveViewCount: LiveData<Int> by lazy {
        ExampleUtils.getLiveInstanceCount(DahLayout::class.java)
    }

    override fun onCreate() {
        Timber.v("onCreate")
        vci.bindViewModel<DahViewModel>(checkNotNull(application))
        vci.bindViewData(LayoutInflater.from(context), this, true)

        vci.viewModel.liveViewModelInstanceCount.observe(vci.lifeCycleOwner, Observer {
            upateText(liveViewCount.value ?: 0, it)
        })
        liveViewCount.observe(vci.lifeCycleOwner, Observer {
            upateText(it, vci.viewModel.liveViewModelInstanceCount.value ?: 0)
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

    private fun upateText(viewCount: Int, viewModelCount: Int) {
        vci.viewDataBinding.dahText.text = "DAH: view = $viewCount, viewModel = $viewModelCount"
    }

    constructor(context: Context, parentFragmentTag: String? = null, @IdRes parentFragmentId: Int? = null) : super(context, parentFragmentTag, parentFragmentId)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
}
