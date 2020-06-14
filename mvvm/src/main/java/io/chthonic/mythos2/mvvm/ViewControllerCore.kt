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

    lateinit var viewModel: VM
        protected set
    lateinit var viewDataBinding: VDB
        protected set

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

    companion object {
        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> activityViewController(
            activity: FragmentActivity
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                activity.viewModelStore,
                activity,
                activity,
                activity.lifecycleScope)
        }

        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> fragmentViewControllerUniqueViewModel(
            fragment: Fragment
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                fragment.viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> fragmentViewControllerSharedViewModel(
            fragment: Fragment
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                checkNotNull(fragment.activity).viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding> compatViewController(
            compat: ViewControllerCompat,
            viewModelStore: ViewModelStore
        ): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(
                viewModelStore,
                compat.savedStateOwner,
                compat.savedStateOwner,
                compat.savedStateOwner.lifecycle.coroutineScope)
        }
    }

    inline fun <reified viewModelType : VM> bindViewModel(application: Application, args: Bundle = Bundle(), fallbackSavedSate: Bundle? = null) {
        Log.d("ViewControllerCore", "bindViewModel: args.keyset = ${args.keySet()}")
        viewModel = ViewModelProvider(viewModelStore, MythosViewModelFactory(application,
            owner = savedStateOwner,
            args = args,
            fallbackSavedSate = fallbackSavedSate)).get(viewModelType::class.java)
    }

    fun bindViewData(
        activity: FragmentActivity,
        @LayoutRes viewDataBindingLayoutRes: Int
    ) {
        bindViewData(DataBindingUtil.setContentView<VDB>(activity, viewDataBindingLayoutRes))
    }

    fun bindViewData(
        layoutInflater: LayoutInflater,
        @LayoutRes viewDataBindingLayoutRes: Int,
        parent: ViewGroup?,
        attachToParent: Boolean
    ) {
        bindViewData(DataBindingUtil.inflate<VDB>(layoutInflater, viewDataBindingLayoutRes, parent, attachToParent))
    }

    fun bindViewData(nuViewDataBinding: VDB) {
        viewDataBinding = nuViewDataBinding
        nuViewDataBinding.lifecycleOwner = lifeCycleOwner
    }
}
