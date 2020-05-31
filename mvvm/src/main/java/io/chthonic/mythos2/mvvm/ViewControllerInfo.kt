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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * Created by jhavatar on 5/24/2020.
 */
open class ViewControllerInfo<VM, VDB>(@LayoutRes private val viewDataBindingLayoutRes: Int,
                                       val viewModelOwner : ViewModelStore,
                                       val lifeCycleOwner : LifecycleOwner,
                                       val savedStateOwner : SavedStateRegistryOwner,
                                       val coroutineScope: CoroutineScope) where VM : MythosViewModel, VDB : ViewDataBinding {

    companion object {
        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> activityViewController(
            activity: FragmentActivity,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerInfo<viewModel, viewDataBinding> {
            return ViewControllerInfo(viewDataBindingLayoutRes,
                activity.viewModelStore,
                activity,
                activity,
                activity.lifecycleScope)
        }

        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> fragmentViewControllerUniqueViewModel(
            fragment: Fragment,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerInfo<viewModel, viewDataBinding> {
            return ViewControllerInfo(viewDataBindingLayoutRes,
                fragment.viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> fragmentViewControllerSharedViewModel(
            fragment: Fragment,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerInfo<viewModel, viewDataBinding> {
            return ViewControllerInfo(viewDataBindingLayoutRes,
                checkNotNull(fragment.activity).viewModelStore,
                fragment.viewLifecycleOwner,
                fragment,
                fragment.viewLifecycleOwner.lifecycleScope)
        }

        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> compatViewControllerUniqueViewModel(
            compat: ViewControllerCompat,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerInfo<viewModel, viewDataBinding> {
            val activity = compat.parentActivity
            val fragment = compat.parentFragment
            return ViewControllerInfo<viewModel, viewDataBinding>(viewDataBindingLayoutRes,
                fragment?.viewModelStore ?: activity.viewModelStore,
                fragment?.viewLifecycleOwner ?: activity,
                fragment ?: activity,
                object : CoroutineScope {
                    override val coroutineContext: CoroutineContext
                        get() = SupervisorJob() + Dispatchers.Main.immediate
                })
        }

        inline fun <reified viewModel: MythosViewModel, reified viewDataBinding: ViewDataBinding> compatViewControllerSharedViewModel(
            compat: ViewControllerCompat,
            @LayoutRes viewDataBindingLayoutRes: Int): ViewControllerInfo<viewModel, viewDataBinding> {
            val activity = compat.parentActivity
            val fragment = compat.parentFragment
            return ViewControllerInfo<viewModel, viewDataBinding>(viewDataBindingLayoutRes,
                activity.viewModelStore,
                fragment?.viewLifecycleOwner ?: activity,
                fragment ?: activity,
                object : CoroutineScope {
                    override val coroutineContext: CoroutineContext
                        get() = SupervisorJob() + Dispatchers.Main.immediate
                })
        }
    }

//    /**
//     * [CoroutineScope] tied to this [Lifecycle].
//     *
//     * This scope will be cancelled when the [Lifecycle] is destroyed.
//     *
//     * This scope is bound to
//     * [Dispatchers.Main.immediate][kotlinx.coroutines.MainCoroutineDispatcher.immediate]
//     */
//    val coroutineScope: CoroutineScope
//        get() = lifeCycleOwner.lifecycle.coroutineScope

    lateinit var viewModel: VM
        protected set
    lateinit var viewDataBinding: VDB
        protected set

    inline fun <reified viewModelType: VM>bindViewModel(application: Application, args: Bundle = Bundle()) {
        viewModel = ViewModelProvider(viewModelOwner,  MythosViewModelFactory(application, savedStateOwner, args)).get(viewModelType::class.java)
    }

    fun bindViewData(activity: Activity) {
        viewDataBinding = DataBindingUtil.setContentView(activity, viewDataBindingLayoutRes)
    }

    fun bindViewData(layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) {
        viewDataBinding = DataBindingUtil.inflate(layoutInflater, viewDataBindingLayoutRes, parent, attachToParent)
    }
}