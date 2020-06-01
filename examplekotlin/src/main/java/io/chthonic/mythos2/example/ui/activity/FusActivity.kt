package io.chthonic.mythos2.example.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.ActivityFusBinding
import io.chthonic.mythos2.mvvm.ViewControllerInfo
import io.chthonic.mythos2.example.ui.fragment.RoFragment
import io.chthonic.mythos2.example.ui.layout.DahLayout
import io.chthonic.mythos2.example.ui.viewmodel.FusViewModel
import io.chthonic.mythos2.example.utils.ExampleUtils

class FusActivity : AppCompatActivity() {

    private val vci: ViewControllerInfo<FusViewModel, ActivityFusBinding> by lazy {
        ViewControllerInfo.activityViewController<FusViewModel, ActivityFusBinding>(this,
            R.layout.activity_fus
        )
    }

    private val liveViewCount: LiveData<Int> by lazy {
        ExampleUtils.getLiveInstanceCount(FusActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vci.bindViewModel<FusViewModel>(application, intent.extras ?: Bundle())
        vci.bindViewData(this)

        vci.viewModel.liveViewModelInstanceCount.observe(vci.lifeCycleOwner, Observer {
            upateText(liveViewCount.value ?: 0, it)
        })
        liveViewCount.observe(vci.lifeCycleOwner, Observer {
            upateText(it, vci.viewModel.liveViewModelInstanceCount.value ?: 0)
        })
        ExampleUtils.notifyInstance(this)


        vci.viewDataBinding.buttonToggleRo.setOnClickListener {
            toggleRo()
        }

        vci.viewDataBinding.buttonToggleDah.setOnClickListener {
            toggleDah()
        }

        showRo()
    }


    private fun toggleRo() {
        val fragment = supportFragmentManager.findFragmentByTag(RoFragment.TAG)
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        } else {
            showRo()
        }
    }

    private fun showRo() {
        supportFragmentManager.beginTransaction()
            .replace(vci.viewDataBinding.fragmentContainer.id, RoFragment.newInstance(), RoFragment.TAG)
            .commitNow()
    }

    private fun toggleDah() {
        if (vci.viewDataBinding.layoutContainer.childCount > 0) {
            vci.viewDataBinding.layoutContainer.removeAllViews()
        } else {
            vci.viewDataBinding.layoutContainer.addView(DahLayout(this))
        }
    }

    private fun upateText(viewCount : Int, viewModelCount: Int) {
        vci.viewDataBinding.fusText.text = "FUS: view = $viewCount, viewModel = $viewModelCount"
    }
}
