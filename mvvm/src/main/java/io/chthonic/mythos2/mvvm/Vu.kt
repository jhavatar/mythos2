package io.chthonic.mythos2.mvvm

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Created by jhavatar on 6/6/2020.
 */
open class Vu<VDB>(val viewDataBinding: VDB, protected val activity: FragmentActivity, protected val fragment: Fragment? = null) where VDB : ViewDataBinding {
    val vdb: VDB
        get() = viewDataBinding

    val root: View
        get() = viewDataBinding.root

    val context: Context
        get() = vdb.root.context

    val resources: Resources
        get() = context.resources
}
