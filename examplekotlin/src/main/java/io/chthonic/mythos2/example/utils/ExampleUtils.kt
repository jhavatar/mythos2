package io.chthonic.mythos2.example.utils

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.chthonic.mythos2.example.R
import timber.log.Timber

/**
 * Created by jhavatar on 5/31/2020.
 */
object ExampleUtils {
    private val hashMap = mutableMapOf<Class<*>, Int>()
    private val countMap = mutableMapOf<Class<*>, Int>()
    private val liveCountMap = mutableMapOf<Class<*>, MutableLiveData<Int>> ()

    @Synchronized
    private fun getInstanceCountMutableObservable(clz: Class<*>): MutableLiveData<Int> {
        val liveData = liveCountMap[clz]
        return if (liveData != null) {
            liveData
        } else {
            val nuLiveData = MutableLiveData(0)
            liveCountMap[clz] = nuLiveData
            nuLiveData
        }
    }

    @Synchronized
    fun getInstanceCountObservable(clz: Class<*>): LiveData<Int> {
        return getInstanceCountMutableObservable(clz)
    }

    @Synchronized
    fun notifyInstance(nuInstance: Any) {
        val clz = nuInstance::class.java
        Timber.v("getInstanceCount $nuInstance, clz = $clz")
        val nuHash = nuInstance.hashCode()
        val oldHash = hashMap[clz]
        val oldCount = countMap.getOrElse(clz, { 0 })
        val nuCount = if (nuHash != oldHash) {
            hashMap[clz] = nuHash
            val nuCount = oldCount + 1
            countMap[clz] = nuCount
            nuCount

        } else {
            oldCount
        }
        Timber.v("notifyInstance: nuCount = $nuCount, clz = $clz")
        getInstanceCountMutableObservable(clz).postValue(nuCount)
    }

    fun upateViewCountText(root: View, viewCount: Int) {
        root.findViewById<TextView>(R.id.text_view).text = "$viewCount,"
    }

}
