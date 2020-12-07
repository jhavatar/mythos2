package io.chthonic.mythos2.example.presentation.ro.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.FragmentRoBinding
import io.chthonic.mythos2.example.presentation.dah.view.layout.DahLayout
import io.chthonic.mythos2.example.presentation.ro.viewmodel.RoViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.ViewControllerCore

class RoFragment : Fragment() {

    private val vci: ViewControllerCore<RoViewModel, FragmentRoBinding> by lazy {
        ViewControllerCore.fragmentViewControllerSharedViewModel(this)
    }

    private val liveViewCount: LiveData<Int> by lazy {
        ExampleUtils.getLiveInstanceCount(RoFragment::class.java)
    }

    companion object {
        fun newInstance(): RoFragment {
            val fragment = RoFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        const val TAG = "RO"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vci.bindViewModel<RoViewModel>(checkNotNull(activity).application)
        vci.bindViewData(
            layoutInflater,
            R.layout.fragment_ro,
            container,
            false
        )
        vci.viewDataBinding.viewmodel = vci.viewModel
        return vci.viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveViewCount.observe(vci.lifeCycleOwner, {
            ExampleUtils.upateViewCountText(vci.vdb.root, it)
        })
        ExampleUtils.notifyInstance(this)

        vci.viewDataBinding.buttonToggleDah.setOnClickListener {
            toggleDah()
        }
    }

    private fun toggleDah() {
        if (vci.viewDataBinding.layoutContainer.childCount > 0) {
            vci.viewDataBinding.layoutContainer.removeAllViews()
        } else {
            this.context?.let {
                vci.viewDataBinding.layoutContainer.addView(DahLayout(it, parentFragmentTag = TAG))
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = Bundle()
        activity?.intent?.extras?.let {
            args.putAll(it)
        }
        this.arguments?.let {
            args.putAll(it)
        }
    }

}
