package io.chthonic.mythos2.example.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.FragmentRoBinding
import io.chthonic.mythos2.example.ui.activity.FusActivity
import io.chthonic.mythos2.mvvm.ViewControllerInfo
import io.chthonic.mythos2.example.ui.layout.DahLayout
import io.chthonic.mythos2.example.ui.viewmodel.RoViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils

class RoFragment : Fragment(){

    private val vci : ViewControllerInfo<RoViewModel, FragmentRoBinding> by lazy  {
        ViewControllerInfo.fragmentViewControllerSharedViewModel<RoViewModel, FragmentRoBinding>(this, R.layout.fragment_ro)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        vci.bindViewModel<RoViewModel>(checkNotNull(activity).application)
        vci.bindViewData(layoutInflater, container, false)
        return vci.viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vci.viewModel.liveViewModelInstanceCount.observe(vci.lifeCycleOwner, Observer {
            upateText(liveViewCount.value ?: 0, it)
        })
        liveViewCount.observe(vci.lifeCycleOwner, Observer {
            upateText(it, vci.viewModel.liveViewModelInstanceCount.value ?: 0)
        })
        ExampleUtils.notifyInstance(this)

        vci.viewDataBinding.buttonToggleDah.setOnClickListener {
            toggleDah()
        }
    }

    private fun toggleDah() {
        if (vci.viewDataBinding.dahLayout.childCount > 0) {
            vci.viewDataBinding.dahLayout.removeAllViews()
        } else {
            this.context?.let {
                vci.viewDataBinding.dahLayout.addView(DahLayout(it, parentFragmentTag = TAG))
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

    private fun upateText(viewCount : Int, viewModelCount: Int) {
        vci.viewDataBinding.roText.text = "RO: view = $viewCount, viewModel = $viewModelCount"
    }
}
