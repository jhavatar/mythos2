package io.chthonic.mythos2.mvvm

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope

/**
 * Created by jhavatar on 5/24/2020.
 */
open class ViewControllerCore<VM, VDB>(
    val viewModelStore: ViewModelStore,
    val lifeCycleOwner: LifecycleOwner,
    val savedStateOwner: SavedStateRegistryOwner,
    val coroutineScope: CoroutineScope
) where VM : MythosViewModel, VDB : ViewDataBinding {

    companion object {

        /**
         * Optimized creation of [ViewControllerCore] for a [FragmentActivity].
         */
        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> activityViewController(
            activity: FragmentActivity
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                activity.viewModelStore,
                activity,
                activity,
                activity.lifecycleScope
            )
        }

        /**
         * Optimized creation of [ViewControllerCore] for a [Fragment] with a unique [MythosViewModel].
         */
        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> fragmentViewControllerUniqueViewModel(
            fragment: Fragment
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                fragment.viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope
            )
        }

        /**
         * Optimized creation of [ViewControllerCore] for a [Fragment] with [MythosViewModel] shared across its [FragmentActivity].
         */
        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> fragmentViewControllerSharedViewModel(
            fragment: Fragment
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                fragment.requireActivity().viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope
            )
        }

        /**
         * Create [ViewControllerCore] for a [ViewControllerCompat] instance
         * with [viewModelStore] storing the [MythosViewModel].
         */
        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> compatViewController(
            compat: ViewControllerCompat,
            viewModelStore: ViewModelStore
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                viewModelStore,
                compat.savedStateOwner,
                compat.savedStateOwner,
                compat.savedStateOwner.lifecycle.coroutineScope
            )
        }
    }

    // region shortened names

    val vms: ViewModelStore
        get() = viewModelStore

    val lco: LifecycleOwner
        get() = lifeCycleOwner

    val sso: SavedStateRegistryOwner
        get() = savedStateOwner

    val crs: CoroutineScope
        get() = coroutineScope

    val vm: VM
        get() = viewModel

    val vdb: VDB
        get() = viewDataBinding

    // endregion

    lateinit var viewModel: VM
        protected set

    @PublishedApi
    internal var accessProtectedViewModelInPublicInline: VM
        get() = viewModel
        set(value) {
            viewModel = value
        }

    lateinit var viewDataBinding: VDB
        protected set

    inline fun <reified viewModelType : VM> bindViewModel(
        application: Application,
        args: Bundle = Bundle(),
        fallbackSavedSate: Bundle? = null
    ) {
        Log.v("ViewControllerCore", "bindViewModel: args.keyset = ${args.keySet()}")
        accessProtectedViewModelInPublicInline = ViewModelProvider(
            viewModelStore,
            MythosViewModelFactory(
                application,
                owner = savedStateOwner,
                args = args,
                fallbackSavedSate = fallbackSavedSate
            )
        ).get(viewModelType::class.java)
    }

    /**
     * Optimized initialize and set of [ViewDataBinding] for a [FragmentActivity].
     */
    fun bindViewData(
        activity: FragmentActivity,
        @LayoutRes viewDataBindingLayoutRes: Int
    ) {
        bindViewData(DataBindingUtil.setContentView(activity, viewDataBindingLayoutRes))
    }

    /**
     * Initialize and set the [ViewDataBinding] from components.
     */
    fun bindViewData(
        layoutInflater: LayoutInflater,
        @LayoutRes viewDataBindingLayoutRes: Int,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) {
        bindViewData(
            DataBindingUtil.inflate(
                layoutInflater,
                viewDataBindingLayoutRes,
                parent,
                attachToParent
            )
        )
    }

    /**
     * Set a [ViewDataBinding].
     */
    fun bindViewData(nuViewDataBinding: VDB) {
        viewDataBinding = nuViewDataBinding
        nuViewDataBinding.lifecycleOwner = lifeCycleOwner
    }

}
