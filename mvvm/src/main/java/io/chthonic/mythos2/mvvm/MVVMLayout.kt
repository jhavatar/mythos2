package io.chthonic.mythos2.mvvm

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.cancelChildren


/**
 * Created by jhavatar on 5/30/2020.
 */
abstract class MVVMLayout<VM, VDB> : FrameLayout, ViewControllerCompat where VM : MythosViewModel, VDB : ViewDataBinding {
    abstract val vci : ViewControllerCore<VM, VDB>
    abstract fun onCreate()
    abstract fun onStart()
    abstract fun onResume()
    abstract fun onPause()
    abstract fun onStop()
    abstract fun onDestroy()

    private val customSavedStateOwner: CustomLifecycleOwner by lazy {
        CustomLifecycleOwner()
    }
    override val savedStateOwner: SavedStateRegistryOwner
        get() = customSavedStateOwner
    override val lifeCycleOwner: LifecycleOwner
        get() = savedStateOwner

    protected var parentFragmentTag: String? = null
        private set

    @IdRes
    protected var parentFragmentId: Int? = null
        private set

    val parentActivity: FragmentActivity?
        get() = context.fragmentActivity() //?: throw Exception("MVVMLayout's parent FragmentActivity not found")

    val defaultViewModelStore: ViewModelStore
        get() = parentFragment?.viewModelStore ?: checkNotNull(parentActivity).viewModelStore

    val application: Application?
        get() = parentActivity?.application

    val parentFragment: Fragment?
        get() = parentFragmentTag?.let {
            parentActivity?.supportFragmentManager?.findFragmentByTag(it)
        } ?: parentFragmentId?.let {
            parentActivity?.supportFragmentManager?.findFragmentById(it)
        }

    private val parentLifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        fun onParentEventStart() {
            Log.v(MVVMLayout::class.simpleName, "onParentEventStart")
            onEventStart()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onParentEventResume() {
            Log.v(MVVMLayout::class.simpleName, "onParentEventResume")
            onEventResume()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onParentEventPause() {
            Log.v(MVVMLayout::class.simpleName, "onParentEventPause")
            onEventPause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onParentEventStop() {
            Log.v(MVVMLayout::class.simpleName, "onParentEventStop")
            onEventStop()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onParentEventDestroy() {
            Log.v(MVVMLayout::class.simpleName, "onParentEventDestroy")
            onEventDestroy()
        }
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
        super.onAttachedToWindow()
        if (!customSavedStateOwner.savedStateRegistryController.savedStateRegistry.isRestored) {
            customSavedStateOwner.savedStateRegistryController.performRestore(Bundle())
        }
        onEventCreate()

        val parentLifecycle = (parentFragment?.viewLifecycleOwner ?: parentActivity?.lifecycleOwner())?.lifecycle
        parentLifecycle?.addObserver(parentLifecycleObserver)
        Log.v(MVVMLayout::class.simpleName, "onAttachedToWindow: parentLifecycle?.currentState = ${parentLifecycle?.currentState}")
        when (parentLifecycle?.currentState) {
            Lifecycle.State.STARTED -> {
                onEventStart()
            }
            null, Lifecycle.State.RESUMED -> {
                onEventStart()
                onEventResume()
            }
            Lifecycle.State.DESTROYED -> {
                onEventStart()
                onEventResume()
                onEventPause()
                onEventStop()
                onEventDestroy()
            }
        }
    }

    override fun onDetachedFromWindow() {
        val parentLifecycle = (parentFragment?.viewLifecycleOwner ?: parentActivity?.lifecycleOwner())?.lifecycle
        parentLifecycle?.removeObserver(parentLifecycleObserver)
        Log.v(MVVMLayout::class.simpleName, "onDetachedFromWindow: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}")
        when (lifeCycleOwner.lifecycle.currentState) {
            Lifecycle.State.RESUMED -> {
                onEventPause()
                onEventStop()
                onEventDestroy()
            }
            Lifecycle.State.STARTED -> {
                onEventStop()
                onEventDestroy()
            }
            else -> {
                onEventDestroy()
            }
        }

        super.onDetachedFromWindow()
    }

    override fun onSaveInstanceState(): Parcelable? {
        return MvvmCustomViewStateWrapper(super.onSaveInstanceState(), Bundle().apply {
            customSavedStateOwner.savedStateRegistryController.performSave(this)
        })
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is MvvmCustomViewStateWrapper) {
            super.onRestoreInstanceState(state.superState)
            customSavedStateOwner.savedStateRegistryController.performRestore(state.state ?: Bundle())

        } else {
            super.onRestoreInstanceState(state)
            customSavedStateOwner.savedStateRegistryController.performRestore(Bundle())
        }
    }

    private fun onEventCreate() {
        Log.v(MVVMLayout::class.simpleName, "onEventCreate: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}")
        if (!lifeCycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            onCreate()
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        } else {
            Log.v(MVVMLayout::class.simpleName, "onEventCreate: IGNORE")
        }
    }

    private fun onEventStart() {
        Log.v(MVVMLayout::class.simpleName, "onEventStart: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}")
        if (!lifeCycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            onStart()
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)

        } else {
            Log.v(MVVMLayout::class.simpleName, "onEventStart: IGNORE")
        }
    }

    private fun onEventResume() {
        Log.v(MVVMLayout::class.simpleName, "onEventResume: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}")
        if (!lifeCycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            onResume()
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        } else {
            Log.v(MVVMLayout::class.simpleName, "onEventResume: IGNORE")
        }
    }

    private fun onEventPause() {
        Log.v(MVVMLayout::class.simpleName, "onEventPause: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}")
        if (lifeCycleOwner.lifecycle.currentState != Lifecycle.State.STARTED) {
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            onPause()

        } else {
            Log.v(MVVMLayout::class.simpleName, "onEventPause: IGNORE")
        }
    }

    private fun onEventStop() {
        Log.v(MVVMLayout::class.simpleName, "onEventStop: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}")
        if (lifeCycleOwner.lifecycle.currentState != Lifecycle.State.CREATED) {
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            onStop()

        } else {
            Log.v(MVVMLayout::class.simpleName, "onEventStop: IGNORE")
        }
    }

    private fun onEventDestroy() {
        Log.v(MVVMLayout::class.simpleName, "onEventDestroy: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}")
        if (lifeCycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            onDestroy()

        } else {
            Log.v(MVVMLayout::class.simpleName, "onEventDestroy: IGNORE")
        }
    }
}

@Parcelize
data class MvvmCustomViewStateWrapper(
    val superState: Parcelable?,
    val state: Bundle?
) : Parcelable