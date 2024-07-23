package com.tbx.base

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.tbx.base.databinding.FragmentViewstubBinding
import com.tbx.base.utils.VersionUtils

/**
 * Created by Trinh BX on 01/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
abstract class BaseViewStubFragment<T : ViewDataBinding> : BaseFragment() {
    private lateinit var stubBinding: FragmentViewstubBinding
    private var mSavedInstanceState: Bundle? = null
    private var hasInflated = false
    private var mViewStub: ViewStub? = null
    private var visible = false
    protected lateinit var binding: T

    protected var screenName:String? = null
    protected abstract fun provideScreenName():String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        stubBinding = FragmentViewstubBinding.inflate(inflater, container, false)
        mViewStub = stubBinding.fragmentViewStub.viewStub
        mViewStub?.layoutResource = getViewStubLayoutResource()
        mSavedInstanceState = savedInstanceState

        if (visible && !hasInflated) {
            val inflatedView = mViewStub?.inflate()
            inflatedView?.let {
                DataBindingUtil.bind<T>(inflatedView)?.let {
                    onCreateViewAfterViewStubInflated(it, inflatedView,
                        mSavedInstanceState)
                    hasInflated = true
                }
            }
        }
        return stubBinding.root
    }

    private fun onCreateViewAfterViewStubInflated(binding: T, inflatedView: View, savedInstanceState: Bundle?){
        screenName = provideScreenName()
        this.binding = binding
        getData()
        registerObserve()
        initView()
        addOnClickListener()
    }
    protected open fun getData(){}
    protected open fun registerObserve(){}
    @LayoutRes
    protected abstract fun getViewStubLayoutResource(): Int
    protected abstract fun initView()
    protected abstract fun addOnClickListener()

    override fun onResume() {
        super.onResume()
        visible = true
        if (mViewStub != null && !hasInflated) {
            val inflatedView = mViewStub?.inflate()
            inflatedView?.let {
                 DataBindingUtil.bind<T>(inflatedView)?.let {
                    onCreateViewAfterViewStubInflated(it, inflatedView,
                        mSavedInstanceState)
                     hasInflated = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hasInflated = false
    }

    override fun onPause() {
        super.onPause()
        visible = false
    }

    override fun onDetach() {
        super.onDetach()
        hasInflated = false
    }

    private var storagePermissionResult: ((Boolean) -> Unit)? = null
    private val requestStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            storagePermissionResult?.invoke(it)
        }

    fun requestStoragePermission(context: Context, storagePermissionResult: ((Boolean) -> Unit)) {
        fun haveStoragePermission(): Boolean {
            return if (VersionUtils.hasMarshmallow()) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }
        this.storagePermissionResult = storagePermissionResult
        if (haveStoragePermission()) {
            this.storagePermissionResult?.invoke(true)

        } else {
            requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}