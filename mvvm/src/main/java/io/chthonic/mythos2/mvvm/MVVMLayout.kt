package io.chthonic.mythos2.mvvm

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.cancelChildren


/**
 * Created by jhavatar on 5/30/2020.
 */
abstract class MVVMLayout<VM, VDB> : FrameLayout, ViewControllerCompat where VM : MythosViewModel, VDB : ViewDataBinding {
    abstract val vci : ViewControllerCore<VM, VDB>
    abstract fun onCreate()
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
        customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)
        customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        onCreate()
    }

    override fun onDetachedFromWindow() {
        customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        customSavedStateOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        onDestroy()
        vci.coroutineScope.coroutineContext.cancelChildren()
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
}

@Parcelize
data class MvvmCustomViewStateWrapper(
    val superState: Parcelable?,
    val state: Bundle?
) : Parcelable