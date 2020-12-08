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
                ExampleUtils.displayViewCountText(viewController.viewDataBinding.root, it)
            }
        )
        ExampleUtils.notifyInstance(this)

        viewController.viewDataBinding.buttonToggleRo.setOnClickListener {
            toggleRo()
        }

        viewController.viewDataBinding.buttonToggleDah.setOnClickListener {
            toggleDah()
        }

        addRo()
    }

    private fun toggleRo() {
        // Remove Ro instance if present, otherwise add new instance.
        if (hasRo()) {
            removeRo()
        } else {
            addRo()
        }
    }

    private fun RoFragment.Companion.findFragmentByTag(): RoFragment? =
        supportFragmentManager.findFragmentByTag(TAG) as? RoFragment

    private fun hasRo(): Boolean {
        return RoFragment.findFragmentByTag() != null
    }

    private fun removeRo() {
        // Remove fragment Ro from layout.
        RoFragment.findFragmentByTag()?.let {
            supportFragmentManager.beginTransaction().remove(it).commit()
        }
    }

    private fun addRo() {
        // Add fragment Ro to layout. If instance exists, replace it.
        supportFragmentManager.beginTransaction()
            .replace(viewController.viewDataBinding.fragmentContainer.id, RoFragment.newInstance(), RoFragment.TAG)
            .commitNow()
    }

    private fun toggleDah() {
        // Remove Dah instance if present, otherwise add new instance.
        if (hasDah()) {
            removeDah()
        } else {
            addDah()
        }
    }

    private fun hasDah(): Boolean {
        return viewController.viewDataBinding.layoutContainer.childCount > 0
    }

    private fun removeDah() {
        // remove Dah view from layout.
        viewController.viewDataBinding.layoutContainer.removeAllViews()
    }

    private fun addDah() {
        // add Dah view to layout.
        viewController.viewDataBinding.layoutContainer.addView(DahLayout(this))
    }

}
