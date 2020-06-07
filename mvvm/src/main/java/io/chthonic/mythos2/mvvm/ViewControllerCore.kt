package io.chthonic.mythos2.mvvm

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
open class ViewControllerCore<VM, VDB, V>(
    val viewModelStore: ViewModelStore,
    val lifeCycleOwner: LifecycleOwner,
    val savedStateOwner: SavedStateRegistryOwner,
    val coroutineScope: CoroutineScope
) where VM : MythosViewModel, VDB : ViewDataBinding, V : Vu<VDB> {

    lateinit var viewModel: VM
        protected set
    lateinit var vu: V
        protected set
    val viewDataBinding: VDB
        get() = vu.viewDataBinding

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
        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding, vu : Vu<viewDataBinding>> activityViewController(
            activity: FragmentActivity
        ): ViewControllerCore<viewModel, viewDataBinding, vu> {
            return ViewControllerCore(
                activity.viewModelStore,
                activity,
                activity,
                activity.lifecycleScope)
        }

        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding, vu : Vu<viewDataBinding>> fragmentViewControllerUniqueViewModel(
            fragment: Fragment
        ): ViewControllerCore<viewModel, viewDataBinding, vu> {
            return ViewControllerCore(
                fragment.viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding, vu : Vu<viewDataBinding>> fragmentViewControllerSharedViewModel(
            fragment: Fragment
        ): ViewControllerCore<viewModel, viewDataBinding, vu> {
            return ViewControllerCore(
                checkNotNull(fragment.activity).viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        fun <viewModel : MythosViewModel, viewDataBinding : ViewDataBinding, vu : Vu<viewDataBinding>> compatViewController(
            compat: ViewControllerCompat,
            viewModelStore: ViewModelStore
        ): ViewControllerCore<viewModel, viewDataBinding, vu> {
            return ViewControllerCore(
                viewModelStore,
                compat.savedStateOwner,
                compat.savedStateOwner,
                compat.savedStateOwner.lifecycle.coroutineScope)
        }
    }

    inline fun <reified viewModelType : VM> bindViewModel(application: Application, args: Bundle = Bundle()) {
        viewModel = ViewModelProvider(viewModelStore, MythosViewModelFactory(application, savedStateOwner, args)).get(viewModelType::class.java)
    }

    fun bindView(
        activity: FragmentActivity,
        @LayoutRes viewDataBindingLayoutRes: Int,
        factory: (viewDataBinding: VDB, activity: FragmentActivity, fragment: Fragment?) -> V
    ) {
        bindView(DataBindingUtil.setContentView<VDB>(activity, viewDataBindingLayoutRes), activity, null, factory)
    }

    fun bindView(
        layoutInflater: LayoutInflater,
        @LayoutRes viewDataBindingLayoutRes: Int,
        parent: ViewGroup?,
        attachToParent: Boolean,
        activity: FragmentActivity,
        fragment: Fragment?,
        factory: (viewDataBinding: VDB, activity: FragmentActivity, fragment: Fragment?) -> V
    ) {
        bindView(DataBindingUtil.inflate<VDB>(layoutInflater, viewDataBindingLayoutRes, parent, attachToParent), activity, fragment, factory)
    }

    fun bindView(
        nuViewDataBinding: VDB,
        activity: FragmentActivity,
        fragment: Fragment?,
        factory: (viewDataBinding: VDB, activity: FragmentActivity, fragment: Fragment?) -> V
    ) {
        nuViewDataBinding.lifecycleOwner = lifeCycleOwner
        vu = factory(nuViewDataBinding, activity, fragment)
    }
}
