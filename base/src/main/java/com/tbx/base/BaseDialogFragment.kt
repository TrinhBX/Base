package com.tbx.base

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentDialog
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.tbx.base.utils.NetWorkUtils
import com.tbx.base.extension.toastMessageShortTime

/**
 * Created by Trinh BX on 02/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
abstract class BaseDialogFragment<VB: ViewBinding>: DialogFragment(){
    var TAG = javaClass.simpleName
    lateinit var binding:VB
    protected var screenName:String? = null

    protected var callback: Callback? = null

    protected abstract fun provideScreenName():String
    abstract fun binding(viewGroup: ViewGroup?, attachToParent: Boolean):VB

    open fun getData(){}
    open fun registerObserve(){}
    abstract fun initView()
    abstract fun initOnClickListener()
    override fun getTheme(): Int {
        return R.style.FullScreenDialog
    }

    private var onBackPressed:(()->Unit)? = null
    protected fun backPress(){
        (dialog as? ComponentDialog)?.onBackPressedDispatcher?.onBackPressed()
    }
    protected fun setOnBackPressedListener(onBackPressed:(()->Unit)){
        this.onBackPressed = onBackPressed
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenName = provideScreenName()
        getData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = binding(container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        callback?.onShow()
        registerObserve()
        initView()
        initOnClickListener()
        (dialog as? ComponentDialog)?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback?.onBackPressed()
                if(onBackPressed==null){
                    dismiss()
                } else{
                    onBackPressed?.invoke()
                }
            }
        })
    }

    private val requestStoragePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        storagePermissionResult?.invoke(it)
    }
    private fun haveStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT in 23..32) {
            context?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestStoragePermission(storagePermissionResult:((Boolean)->Unit)){
        this.storagePermissionResult = storagePermissionResult
        if (haveStoragePermission()){
            this.storagePermissionResult?.invoke(true)

        } else{
            requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
    private var storagePermissionResult:((Boolean)->Unit)? = null

    protected fun hideSystemUi() {
        if(activity?.supportFragmentManager?.isDestroyed==true || activity?.supportFragmentManager?.isStateSaved==true) return
        dialog?.window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
            WindowInsetsControllerCompat(it, binding.root).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    fun viewFullScreen(isNavDark:Boolean = false) {
        dialog?.window?.apply {
            decorView.systemUiVisibility = if (isNavDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            } else {
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
            statusBarColor = Color.TRANSPARENT
        }
    }

    fun setNavDarkColor(isNavDark:Boolean = false) {
        dialog?.window?.apply {
            if (isNavDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isNavDark) {
                decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }



    protected fun changeSystemUiColor(newColor:Int){
        dialog?.window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            navigationBarColor = newColor
            statusBarColor  = newColor
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callback?.onDismiss()
    }

    interface Callback {
        fun onDismiss() {}
        fun onShow() {}

        fun onBackPressed(){}
    }

    protected fun dismissSafe(){
        if(activity?.supportFragmentManager?.isStateSaved != true){
            dismiss()
        }
    }

    fun checkInternet(isInternet: () -> Unit, error: (() -> Unit)? = null) {
        context?.let {
            if (!NetWorkUtils.isNetworkConnected(it)) {
                it.toastMessageShortTime("Please check your internet connection")
                error?.invoke()
            } else {
                isInternet.invoke()
            }
        }
    }
}