package io.chthonic.mythos2.example.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

/**
 * Created by jhavatar on 5/31/2020.
 */
object ExampleUtils {
    private val hashMap = mutableMapOf<Class<*>, Int>()
    private val countMap = mutableMapOf<Class<*>, Int>()
    private val liveCountMap = mutableMapOf<Class<*>, MutableLiveData<Int>> ()

    @Synchronized
    private fun getMutableLiveInstanceCount(clz: Class<*>): MutableLiveData<Int> {
        val liveData = liveCountMap[clz]
        return if (liveData != null) {
            liveData
        } else {
            val nuLiveData = MutableLiveData<Int>(0)
            liveCountMap[clz] = nuLiveData
            nuLiveData
        }
    }

    @Synchronized
    fun getLiveInstanceCount(clz: Class<*>): LiveData<Int> {
        return getMutableLiveInstanceCount(clz)
    }

    @Synchronized
    fun notifyInstance(nuInstance: Any) {
        val clz = nuInstance::class.java
        Timber.d("getInstanceCount $nuInstance, clz = $clz")
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
        Timber.d("notifyInstance: nuCount = $nuCount, clz = $clz")
        getMutableLiveInstanceCount(clz).postValue(nuCount)
    }
}
