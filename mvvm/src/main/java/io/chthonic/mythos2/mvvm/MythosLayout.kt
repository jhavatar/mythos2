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

/**
 * Created by jhavatar on 5/30/2020.
 */
abstract class MythosLayout<VM, VDB> : FrameLayout, ViewControllerCompat
        where VM : MythosViewModel, VDB : ViewDataBinding {

    // region abstract

    protected abstract val viewController: ViewControllerCore<VM, VDB>

    abstract fun onCreate()

    abstract fun onStart()

    abstract fun onResume()

    abstract fun onPause()

    abstract fun onStop()

    abstract fun onDestroy()

    // endregion

    val parentActivity: FragmentActivity?
        get() = context.fragmentActivity()

    val defaultViewModelStore: ViewModelStore
        get() = parentFragment?.viewModelStore ?: requireParentActivity().viewModelStore

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
            Log.v(MythosLayout::class.simpleName, "onParentEventStart")
            onEventStart()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onParentEventResume() {
            Log.v(MythosLayout::class.simpleName, "onParentEventResume")
            onEventResume()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onParentEventPause() {
            Log.v(MythosLayout::class.simpleName, "onParentEventPause")
            onEventPause()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onParentEventStop() {
            Log.v(MythosLayout::class.simpleName, "onParentEventStop")
            onEventStop()
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onParentEventDestroy() {
            Log.v(MythosLayout::class.simpleName, "onParentEventDestroy")
            onEventDestroy()
        }
    }

    protected val viewModel: VM
        get() = viewController.viewModel

    protected val viewDataBinding: VDB
        get() = viewController.vdb

    override val savedStateOwner: SavedStateRegistryOwner
        get() = customSavedStateOwner

    override val lifeCycleOwner: LifecycleOwner
        get() = savedStateOwner

    private val customSavedStateOwner: MythosCustomLifecycleOwner by lazy {
        MythosCustomLifecycleOwner()
    }

    protected var parentFragmentTag: String? = null
        private set

    @IdRes
    protected var parentFragmentId: Int? = null
        private set

    @JvmOverloads
    constructor(context: Context, parentFragmentTag: String? = null, @IdRes parentFragmentId: Int? = null) :
        super(context) {
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
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initAttrs(context, attrs, defStyleAttr = defStyleAttr, defStyleRes = defStyleRes)
    }

    fun requireParentActivity(): FragmentActivity {
        return parentActivity
            ?: throw IllegalStateException("MythosLayout $this not attached to an activity.")
    }

    private fun initAttrs(context: Context?, attrs: AttributeSet?, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        if (attrs != null) {
            val ta = context?.obtainStyledAttributes(
                attrs,
                R.styleable.MythosLayout,
                defStyleAttr,
                defStyleRes
            )

            parentFragmentTag = ta?.getString(R.styleable.MythosLayout_mythos_parentfragment_tag) ?: parentFragmentTag
            parentFragmentId =
                (ta?.getResourceId(R.styleable.MythosLayout_mythos_parentfragment_id, 0) ?: parentFragmentId).let {
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
        Log.v(
            MythosLayout::class.simpleName,
            "onAttachedToWindow: parentLifecycle?.currentState = ${parentLifecycle?.currentState}"
        )
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
            else -> { // no-op to exhaust when
            }
        }
    }

    override fun onDetachedFromWindow() {
        val parentLifecycle = (parentFragment?.viewLifecycleOwner ?: parentActivity?.lifecycleOwner())?.lifecycle
        parentLifecycle?.removeObserver(parentLifecycleObserver)
        Log.v(
            MythosLayout::class.simpleName,
            "onDetachedFromWindow: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}"
        )
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
        return MythosCustomViewStateWrapper(
            super.onSaveInstanceState(),
            Bundle().apply {
                customSavedStateOwner.savedStateRegistryController.performSave(this)
            }
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is MythosCustomViewStateWrapper) {
            super.onRestoreInstanceState(state.superState)
            customSavedStateOwner.savedStateRegistryController.performRestore(state.state ?: Bundle())

        } else {
            super.onRestoreInstanceState(state)
            customSavedStateOwner.savedStateRegistryController.performRestore(Bundle())
        }
    }

    private fun onEventCreate() {
        Log.v(
            MythosLayout::class.simpleName,
            "onEventCreate: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}"
        )
        if (!lifeCycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            onCreate()
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        } else {
            Log.v(MythosLayout::class.simpleName, "onEventCreate: IGNORE")
        }
    }

    private fun onEventStart() {
        Log.v(
            MythosLayout::class.simpleName,
            "onEventStart: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}"
        )
        if (!lifeCycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            onStart()
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)

        } else {
            Log.v(MythosLayout::class.simpleName, "onEventStart: IGNORE")
        }
    }

    private fun onEventResume() {
        Log.v(
            MythosLayout::class.simpleName,
            "onEventResume: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}"
        )
        if (!lifeCycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            onResume()
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        } else {
            Log.v(MythosLayout::class.simpleName, "onEventResume: IGNORE")
        }
    }

    private fun onEventPause() {
        Log.v(
            MythosLayout::class.simpleName,
            "onEventPause: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}"
        )
        if (lifeCycleOwner.lifecycle.currentState != Lifecycle.State.STARTED) {
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            onPause()

        } else {
            Log.v(MythosLayout::class.simpleName, "onEventPause: IGNORE")
        }
    }

    private fun onEventStop() {
        Log.v(
            MythosLayout::class.simpleName,
            "onEventStop: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}"
        )
        if (lifeCycleOwner.lifecycle.currentState != Lifecycle.State.CREATED) {
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
            onStop()

        } else {
            Log.v(MythosLayout::class.simpleName, "onEventStop: IGNORE")
        }
    }

    private fun onEventDestroy() {
        Log.v(
            MythosLayout::class.simpleName,
            "onEventDestroy: lifecycle.currentState = ${lifeCycleOwner.lifecycle.currentState}"
        )
        if (lifeCycleOwner.lifecycle.currentState != Lifecycle.State.DESTROYED) {
            customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            onDestroy()

        } else {
            Log.v(MythosLayout::class.simpleName, "onEventDestroy: IGNORE")
        }
    }

}

@Parcelize
data class MythosCustomViewStateWrapper(
    val superState: Parcelable?,
    val state: Bundle?
) : Parcelable
