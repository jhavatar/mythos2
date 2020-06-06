package io.chthonic.mythos2.example.ui.vu

import android.widget.TextView
import androidx.databinding.ViewDataBinding
import io.chthonic.mythos2.example.R
import io.chthonic.mythos2.mvvm.Vu

/**
 * Created by jhavatar on 6/7/2020.
 */
class ExampleVu<VDB>(vdb: VDB) : Vu<VDB>(vdb) where VDB : ViewDataBinding {
    private val viewCountTextView: TextView by lazy {
        vdb.root.findViewById<TextView>(R.id.text_view)
    }

    fun upateText(viewCount: Int) {
        viewCountTextView.text = "$viewCount,"
    }
}
