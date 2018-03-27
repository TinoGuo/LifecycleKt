package me.tino.lifecyclekt

import android.app.Application
import timber.log.Timber

/**
 * mailTo:guocheng@xuxu.in
 * Created by tino on 2018 March 23, 17:14.
 */
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}