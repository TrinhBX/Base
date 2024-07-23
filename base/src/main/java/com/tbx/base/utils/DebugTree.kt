package com.tbx.base.utils

import timber.log.Timber

/**
 * Created by Trinh BX on 01/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */

class DebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format("C:%s:%s",
            super.createStackElementTag(element),
            element.lineNumber
        )
    }
}