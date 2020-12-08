package io.chthonic.mythos2.example.presentation.fus.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.ActivityFusBinding
import io.chthonic.mythos2.example.presentation.dah.view.layout.DahLayout
import io.chthonic.mythos2.example.presentation.fus.viewmodel.FusViewModel
import io.chthonic.mythos2.example.presentation.ro.view.fragment.RoFragment
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.ViewControllerCore

class FusActivity : AppCompatActivity() {

    private val vci: ViewControllerCore<FusViewModel, ActivityFusBinding> by lazy {
        ViewControllerCore.activityViewController(this)
    }

    private val liveViewCount: LiveData<Int> by lazy {
        ExampleUtils.getLiveInstanceCount(FusActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vci.bindViewModel<FusViewModel>(application, intent.extras ?: Bundle())
        vci.bindViewData(this, R.layout.activity_fus)
        vci.viewDataBinding.viewmodel = vci.viewModel

        liveViewCount.observe(
            vci.lifeCycleOwner,
            {
                ExampleUtils.upateViewCountText(vci.vdb.root, it)
            }
        )
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
