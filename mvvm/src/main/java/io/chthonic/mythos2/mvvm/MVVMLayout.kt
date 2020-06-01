package io.chthonic.mythos2.mvvm

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.cancelChildren

/**
 * Created by jhavatar on 5/30/2020.
 */
abstract class MVVMLayout<VM, VDB> : FrameLayout, ViewControllerCompat where VM : MythosViewModel, VDB : ViewDataBinding {

    abstract val vci : ViewControllerInfo<VM, VDB>

    abstract fun onCreate()
    abstract fun onDestroy()

    protected var parentFragmentTag: String? = null
        private set

    @IdRes
    protected var parentFragmentId: Int? = null
        private set

    val application: Application
        get() = parentActivity.application

    override val parentActivity: FragmentActivity
        get() = context.fragmentActivity() ?: throw Exception("MVVMLayout's parent FragmentActivity not found")

    private val customLifecycleOwner = CustomLifecycleOwner()
    override val lifeCycleOwner: LifecycleOwner
        get() = customLifecycleOwner

    override val parentFragment: Fragment?
        get() = parentFragmentTag?.let {
            parentActivity.supportFragmentManager.findFragmentByTag(it)
        } ?: parentFragmentId?.let {
            parentActivity.supportFragmentManager.findFragmentById(it)
        }

    @JvmOverloads
    constructor(context: Context, parentFragmentTag: String? = null, @IdRes parentFragmentId: Int? = null) : super(context) {
        this.parentFragmentTag = parentFragmentTag
        this.parentFragmentId = parentFragmentId
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs, defStyleAttr = defStyleAttr)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrs(context, attrs, defStyleAttr = defStyleAttr, defStyleRes = defStyleRes)
    }


    private fun initAttrs(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        if (attrs != null) {
            val ta = context?.obtainStyledAttributes(
                attrs,
                R.styleable.MVVMLayout,
                defStyleAttr,
                defStyleRes)

            parentFragmentTag = ta?.getString(R.styleable.MVVMLayout_mvvmlayout_parentfragment_tag) ?: parentFragmentTag
            parentFragmentId = (ta?.getResourceId(R.styleable.MVVMLayout_mvvmlayout_parentfragment_id, 0) ?: parentFragmentId).let {
                if (it == 0) {
                    null
                } else {
                    it
                }
            }
            ta?.recycle()
        }
    }

    override fun onAttachedToWindow() {
        onCreate()
        customLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        customLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        customLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        customLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        customLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        customLifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        onDestroy()
        vci.coroutineScope.coroutineContext.cancelChildren()
        super.onDetachedFromWindow()
    }
}