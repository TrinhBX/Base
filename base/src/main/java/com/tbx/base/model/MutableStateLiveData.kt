package com.tbx.base.model

import androidx.lifecycle.MutableLiveData

/**
 * Created by Trinh BX on 02/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
class MutableStateLiveData<T> : MutableLiveData<StateData<T>>() {
    fun postLoading() {
        postValue(StateData<T>().loading())
    }

    fun postError(errorMsg: String?) {
        postValue(StateData<T>().error(errorMsg ?: ""))
    }

    fun postErrorData(errorData: T) {
        postValue(StateData<T>().error(errorData))
    }

    fun postSuccess(data: T) {
        postValue(StateData<T>().success(data))
    }

}