package com.tbx.base

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.tbx.base.utils.NetWorkUtils
import com.tbx.base.extension.getStatusBarHeight
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException


/**
 * Created by Trinh BX on 01/02/2023.
 * Mail: trinhbx196@gmail.com.
 * Phone: +084 988 324 622.
 */
abstract class BaseActivity<VB:ViewBinding>:AppCompatActivity() {
    protected lateinit var binding:VB

    var dialogNetWork: Dialog? = null

    protected var screenName:String? = null
    private var onBackPressed:(()->Unit)? = null
    protected abstract fun provideScreenName():String
    abstract fun binding(): VB
    open fun getData(){}
    abstract fun initView()
    open fun initOnClickListener(){}
    open fun registerObserve(){}

    protected open fun registerEventBus() = false
    protected fun setOnBackPressedListener(onBackPressed:(()->Unit)){
        this.onBackPressed = onBackPressed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        binding = binding()
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(onBackPressed==null){
                    finish()
                } else{
                    onBackPressed?.invoke()
                }
            }
        })
        getData()
        screenName = provideScreenName()
        registerObserve()
        initView()
        initOnClickListener()
//        showDialogNetWork()
    }

    fun viewFullScreen() {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        try {
            val hStatusBar = getStatusBarHeight()
            fun supportSetMargin(view:View?){
                (view?.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
                    if (hStatusBar > 0) {
                        topMargin = hStatusBar
                    }
                }
            }
//            supportSetMargin(binding.root.findViewById(R.id.imgBack))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val requestStoragePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        storagePermissionResult?.invoke(it)
    }
    private val requestStoragePermissionApi33 = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        val result = it.filter { r -> !r.value }.isEmpty()
        storagePermissionResult?.invoke(result)
    }
    private fun haveStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if(Build.VERSION.SDK_INT >= 33) {
                val p1 = checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
                val p2 = checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
                p1 && p2
            } else {
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            true
        }
    }

    fun requestStoragePermission(storagePermissionResult:((Boolean)->Unit)){
        this.storagePermissionResult = storagePermissionResult
        if (haveStoragePermission()){
            this.storagePermissionResult?.invoke(true)
        } else{
            if (Build.VERSION.SDK_INT >= 33)  {
                requestStoragePermissionApi33.launch(arrayOf(Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_IMAGES)  )
            } else {
                requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
    private var storagePermissionResult:((Boolean)->Unit)? = null

    protected fun hideSystemUi() {
        if(supportFragmentManager.isDestroyed || supportFragmentManager.isStateSaved) return
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                onActivityForResult(intent = data, onResultCode = result.resultCode)
            } else {
                onActivityForResult(onResultCode = result.resultCode)
            }
        }

    open fun onActivityForResult(
        intent: Intent? = null,
        onResultCode: Int? = null,
    ) {

    }

    fun startActivityForResult(intent: Intent) {
        launchSomeActivity.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        try {
            if (registerEventBus() && !EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this)
            }
        } catch (ex: EventBusException) {
            ex.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Glide.get(this).clearMemory()
        mAlertDialog?.dismiss()
        mAlertDialog = null
        if (registerEventBus() && EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }


    protected fun changeSystemUiColor(newColor:Int){
        window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            navigationBarColor = newColor
            statusBarColor  = newColor
        }
    }

    protected fun changeStatusBarColor(newColor:Int){
        window?.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = try {
                ContextCompat.getColor(this@BaseActivity, newColor)
            } catch (e: Resources.NotFoundException) {
                newColor
            }
        }
    }
    protected fun changeStatusBarColor(newColor:String){
        try {
            changeStatusBarColor(Color.parseColor(newColor))
        } catch (e:Exception){
            e.printStackTrace()
        }
    }

    private var mAlertDialog:AlertDialog? = null

    /**
    * null value will dismiss current AlertDialog
    * */
    fun changeCurAlertDialog(newDialog: AlertDialog?){
        mAlertDialog?.dismiss()
        mAlertDialog = null
        mAlertDialog = newDialog
    }
    private fun showDialogNetWork() {
        if (!NetWorkUtils.isNetworkConnected(this)) {
//            if (provideScreenName() != VIEW_INTRO_ACTIVITY) {
//                NetWorkDialogFragment.newInstance(supportFragmentManager)
//            }
        }
    }


//    fun checkInternet(isInternet: () -> Unit, error: (() -> Unit)? = null) {
//        if (!isNetworkConnected(this)) {
//            toastMessageShortTime("Please check your internet connection")
//            error?.invoke()
//        } else {
//            isInternet.invoke()
//        }
//    }
}