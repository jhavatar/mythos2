package io.chthonic.mythos2.example.presentation.fus.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.example.databinding.ActivityFusBinding
import io.chthonic.mythos2.example.presentation.dah.view.layout.DahLayout
import io.chthonic.mythos2.example.presentation.fus.viewmodel.FusViewModel
import io.chthonic.mythos2.example.presentation.ro.view.fragment.RoFragment
import io.chthonic.mythos2.example.utils.ExampleUtils
import io.chthonic.mythos2.mvvm.ViewControllerCore

class FusActivity : AppCompatActivity() {

    private val viewController: ViewControllerCore<FusViewModel, ActivityFusBinding> by lazy {
        ViewControllerCore.activityViewController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewController.bindViewModel<FusViewModel>(application, intent.extras ?: Bundle())
        viewController.bindViewData(this, R.layout.activity_fus)
        viewController.viewDataBinding.viewmodel = viewController.viewModel

        viewController.viewModel.getViewInstanceCountObservable(FusActivity::class.java).observe(
            viewController.lifeCycleOwner,
            {
                ExampleUtils.upateViewCountText(viewController.vdb.root, it)
            }
        )
        ExampleUtils.notifyInstance(this)

        viewController.viewDataBinding.buttonToggleRo.setOnClickListener {
            toggleRo()
        }

        viewController.viewDataBinding.buttonToggleDah.setOnClickListener {
            toggleDah()
        }

        showRo()
    }

    private fun RoFragment.Companion.findFragmentByTag(): RoFragment? =
        supportFragmentManager.findFragmentByTag(TAG) as? RoFragment

    private fun toggleRo() {
        val fragment = RoFragment.findFragmentByTag()
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        } else {
            showRo()
        }
    }

    private fun showRo() {
        supportFragmentManager.beginTransaction()
            .replace(viewController.viewDataBinding.fragmentContainer.id, RoFragment.newInstance(), RoFragment.TAG)
            .commitNow()
    }

    private fun toggleDah() {
        if (viewController.viewDataBinding.layoutContainer.childCount > 0) {
            viewController.viewDataBinding.layoutContainer.removeAllViews()
        } else {
            viewController.viewDataBinding.layoutContainer.addView(DahLayout(this))
        }
    }

}
