package io.chthonic.mythos2.mvvm

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Created by jhavatar on 5/30/2020.
 */
interface ViewControllerCompat {
    val parentActivity: FragmentActivity
    val parentFragment: Fragment?
}