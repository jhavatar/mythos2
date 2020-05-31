package io.chthonic.mythos2.example.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vci.bindViewModel<FusViewModel>(application, intent.extras ?: Bundle())
        vci.bindViewData(this)

        showRo()

        vci.viewDataBinding.buttonToggleRo.setOnClickListener {
            toggleRo()
        }

        vci.viewDataBinding.buttonToggleDah.setOnClickListener {
            toggleDah()
        }
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
        if (vci.viewDataBinding.dahLayout.childCount > 0) {
            vci.viewDataBinding.dahLayout.removeAllViews()
        } else {
            vci.viewDataBinding.dahLayout.addView(DahLayout(this))
        }
    }

    override fun onResume() {
        super.onResume()
        vci.viewDataBinding.fusText.text = "view = ${ExampleUtils.getInstanceCount(this, this::class.java)}, viewModel = ${vci.viewModel.instanceCount}"
    }
}
