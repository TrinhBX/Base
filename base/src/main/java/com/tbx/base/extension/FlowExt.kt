package com.tbx.base.extension

import com.tbx.base.model.MutableStateLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

/**
 * Created by Trinh BX on 02/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
suspend fun <T> Flow<T>.collectToSateLiveData(liveData: MutableStateLiveData<T>) {
    this.flowOn(Dispatchers.IO)
        .catch {
            liveData.postError(it.message)
        }
        .onStart {
            liveData.postLoading()
        }.collect {
            liveData.postSuccess(it)
        }
}

suspend fun <T> Flow<T>.collectLatestToSateLiveData(liveData: MutableStateLiveData<T>) {
    this.flowOn(Dispatchers.IO)
        .catch {
            liveData.postError(it.message)
        }
        .onStart {
            liveData.postLoading()
        }.collectLatest {
            liveData.postSuccess(it)
        }
}