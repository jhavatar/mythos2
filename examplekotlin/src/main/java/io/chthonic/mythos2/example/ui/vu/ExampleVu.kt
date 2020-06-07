package io.chthonic.mythos2.example.ui.vu

import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.mvvm.Vu

/**
 * Created by jhavatar on 6/7/2020.
 */
class ExampleVu<VDB>(
    viewDataBinding: VDB,
    activity: FragmentActivity,
    fragment: Fragment? = null
) : Vu<VDB>(viewDataBinding, activity, fragment) where VDB : ViewDataBinding {
    private val viewCountTextView: TextView by lazy {
        vdb.root.findViewById<TextView>(R.id.text_view)
    }

    fun upateText(viewCount: Int) {
        viewCountTextView.text = "$viewCount,"
    }
}
