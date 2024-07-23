package com.tbx.base.utils


/**
 * @author Created by TrinhBX.
 * Mail: trinhbx196@gmail.com
 * Phone: +08 988324622
 * @since Date: 9/11/23
 **/

interface DownloadFileCallback {
    fun onDownloadSuccess(file: String) {}
    fun onProgressUpdate(progress: Int) {}
    fun onDownloadFail(e: Exception?) {}
}