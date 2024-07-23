package com.tbx.base

import android.app.Application
import com.tbx.base.utils.DebugTree
import timber.log.Timber

/**
 * @author
 * Created by Trinh BX.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 * @since 11/07/2024
 */

open class BaseApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        configTimber()
    }

    private fun configTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}