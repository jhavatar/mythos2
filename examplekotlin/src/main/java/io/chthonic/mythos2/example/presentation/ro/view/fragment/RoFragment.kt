package io.chthonic.mythos2.example.presentation.ro.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.FragmentRoBinding
import io.chthonic.mythos2.example.presentation.dah.view.layout.DahLayout
import io.chthonic.mythos2.example.presentation.ro.viewmodel.RoViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.ViewControllerCore

class RoFragment : Fragment() {

    companion object {
        const val TAG = "RO"

        fun newInstance(): RoFragment {
            val fragment = RoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewController: ViewControllerCore<RoViewModel, FragmentRoBinding> by lazy {
        ViewControllerCore.fragmentViewControllerSharedViewModel(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewController.bindViewModel<RoViewModel>(requireActivity().application)
        viewController.bindViewData(
            layoutInflater,
            R.layout.fragment_ro,
            container,
            false
        )
        viewController.viewDataBinding.viewmodel = viewController.viewModel
        return viewController.viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewController.viewModel.getViewInstanceCountObservable(RoFragment::class.java).observe(
            viewController.lifeCycleOwner,
            {
                ExampleUtils.upateViewCountText(viewController.vdb.root, it)
            }
        )
        ExampleUtils.notifyInstance(this)

        viewController.viewDataBinding.buttonToggleDah.setOnClickListener {
            toggleDah()
        }
    }

    private fun toggleDah() {
        if (viewController.viewDataBinding.layoutContainer.childCount > 0) {
            viewController.viewDataBinding.layoutContainer.removeAllViews()
        } else {
            this.context?.let {
                viewController.viewDataBinding.layoutContainer.addView(DahLayout(it, parentFragmentTag = TAG))
            }
        }
    }

}
