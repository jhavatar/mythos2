package io.chthonic.mythos2.mvvm

import android.app.Activity
import android.app.Application
import android.os.Bundle
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
open class ViewControllerCore<VM, VDB>(@LayoutRes private val viewDataBindingLayoutRes: Int,
                                       val viewModelStore : ViewModelStore,
                                       val lifeCycleOwner : LifecycleOwner,
                                       val savedStateOwner : SavedStateRegistryOwner,
                                       val coroutineScope: CoroutineScope) where VM : MythosViewModel, VDB : ViewDataBinding {

    companion object {
        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> activityViewController(
            activity: FragmentActivity,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(viewDataBindingLayoutRes,
                activity.viewModelStore,
                activity,
                activity,
                activity.lifecycleScope)
        }

        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> fragmentViewControllerUniqueViewModel(
            fragment: Fragment,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(viewDataBindingLayoutRes,
                fragment.viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> fragmentViewControllerSharedViewModel(
            fragment: Fragment,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore(viewDataBindingLayoutRes,
                checkNotNull(fragment.activity).viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> compatViewController(
            compat: ViewControllerCompat,
            @LayoutRes viewDataBindingLayoutRes: Int,
            viewModelStore : ViewModelStore): ViewControllerCore<viewModel, viewDataBinding> {
            return ViewControllerCore<viewModel, viewDataBinding>(viewDataBindingLayoutRes,
                viewModelStore,
                compat.savedStateOwner,
                compat.savedStateOwner,
                compat.savedStateOwner.lifecycle.coroutineScope)
        }
    }

    lateinit var viewModel: VM
        protected set
    lateinit var viewDataBinding: VDB
        protected set

    inline fun <reified viewModelType: VM>bindViewModel(application: Application, args: Bundle = Bundle()) {
        viewModel = ViewModelProvider(viewModelStore,  MythosViewModelFactory(application, savedStateOwner, args)).get(viewModelType::class.java)
    }

    fun bindViewData(activity: Activity) {
        bindViewData(DataBindingUtil.setContentView<VDB>(activity, viewDataBindingLayoutRes))
    }

    fun bindViewData(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) {
        bindViewData(DataBindingUtil.inflate<VDB>(layoutInflater, viewDataBindingLayoutRes, parent, attachToParent))
    }

    fun bindViewData(nuViewDataBinding: VDB) {
        viewDataBinding = nuViewDataBinding
        viewDataBinding.lifecycleOwner = lifeCycleOwner
    }
}