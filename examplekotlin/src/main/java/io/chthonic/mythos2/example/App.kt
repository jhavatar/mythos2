package io.chthonic.mythos2.example

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * Created by jhavatar on 3/15/2020.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}