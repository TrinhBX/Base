package com.tbx.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.tbx.base.extension.pxToDp


/**
 * @author Created by TrinhBX.
 * Mail: trinhbx196@gmail.com
 * Phone: +08 988324622
 * @since Date: 9/14/23
 **/

abstract class BaseDialogFullScreenFragment<VB: ViewBinding>:BaseDialogFragment<VB>() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = binding(container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.window?.let {
            WindowCompat.setDecorFitsSystemWindows(it, false)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _: View?, insets: WindowInsetsCompat ->
            val statusBarHeight =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top // in px
            val navigationBarHeight =
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom // in px
            onStatusBarHeightCalculated(pxToDp(statusBarHeight))
            onNavigationBarHeightCalculated(navigationBarHeight)
            WindowInsetsCompat.CONSUMED
        }
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.FullScreenDialog_Transparent
    }

    protected abstract fun onStatusBarHeightCalculated(height:Int)
    protected abstract fun onNavigationBarHeightCalculated(height:Int)
}