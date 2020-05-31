package io.chthonic.mythos2.example.utils

import timber.log.Timber

/**
 * Created by jhavatar on 5/31/2020.
 */
object ExampleUtils {
    private val hashMap = mutableMapOf<Class<*>, Int>()
    private val countMap = mutableMapOf<Class<*>, Int>()

    @Synchronized
    fun getInstanceCount(nuInstance: Any, clz: Class<*>): Int  {
        Timber.d("getInstanceCount $nuInstance, clz = $clz")
        val nuHash = nuInstance.hashCode()
        val oldHash = hashMap[clz]
        val oldCount = countMap.getOrElse(clz, { 0 })
        return if (nuHash != oldHash) {
            hashMap[clz] = nuHash
            val nuCount = oldCount + 1
            countMap[clz] = nuCount
            nuCount

        } else {
            oldCount
        }
    }
}