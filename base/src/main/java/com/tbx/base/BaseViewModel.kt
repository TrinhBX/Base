package com.tbx.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * Created by Trinh BX on 01/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */

open class BaseViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _showMessageLiveData = MutableLiveData<String?>()
    val showMessageLiveData: LiveData<String?> = _showMessageLiveData

    protected fun showLoading(isShow: Boolean) {
        _isLoading.postValue(isShow)
    }

    protected fun showMessage(message: String?) {
        _showMessageLiveData.postValue(message)
    }
}