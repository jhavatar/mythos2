package io.chthonic.mythos2.example.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.ActivityFusBinding
import io.chthonic.mythos2.example.ui.fragment.RoFragment
import io.chthonic.mythos2.example.ui.layout.DahLayout
import io.chthonic.mythos2.example.ui.viewmodel.FusViewModel
import io.chthonic.mythos2.example.ui.vu.ExampleVu
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.ViewControllerCore

class FusActivity : AppCompatActivity() {

    private val vci: ViewControllerCore<FusViewModel, ActivityFusBinding, ExampleVu<ActivityFusBinding>> by lazy {
        ViewControllerCore.activityViewController<FusViewModel, ActivityFusBinding, ExampleVu<ActivityFusBinding>>(this)
    }

    private val liveViewCount: LiveData<Int> by lazy {
        ExampleUtils.getLiveInstanceCount(FusActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vci.bindViewModel<FusViewModel>(application, intent.extras ?: Bundle())
        vci.bindViewData(this, R.layout.activity_fus, ::ExampleVu)
        vci.viewDataBinding.viewmodel = vci.viewModel

        liveViewCount.observe(vci.lifeCycleOwner, Observer {
            vci.vu.upateText(it)
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
}
