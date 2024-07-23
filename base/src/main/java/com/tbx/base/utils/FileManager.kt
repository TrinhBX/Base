package com.tbx.base.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment.*
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.*
import java.nio.channels.FileChannel
import kotlin.math.roundToInt


/**
 * @author Created by TrinhBX.
 * Mail: trinhbx196@gmail.com
 * Phone: +08 988324622
 * @since Date: 9/11/23
 **/
object FileManager {
    //INTERNAL STORAGE
    private fun createFolderInternal(context: Context,folderName:String){
        val mFolder = File("${context.filesDir}/$folderName")
        if (!mFolder.exists()) {
            mFolder.mkdir()
        }
    }
    fun getFilePathInternal(context: Context,rootFolderName:String,fileName:String?):String{
        createFolderInternal(context,rootFolderName)
        val rootPath = context.filesDir.path
        return if (fileName==null){
            "$rootPath/$rootFolderName"
        } else{
            "$rootPath/$rootFolderName/$fileName"
        }
    }



    private fun byteToKB(size: Int): Float {
        return ((size.toDouble() / 10).roundToInt() / 100f)
    }
    private fun byteToMB(size: Int):Float{
        return ((size.toDouble() / (10*1000)).roundToInt() / 100f)
    }
    fun getPathFromURI(context: Context, uri: Uri): String? {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
//                val column_index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(cursor.getColumnIndexOrThrow(column))
            }
        } catch (e:Exception){
            return null
        }
        finally {
            cursor?.close()
        }
        return null
    }
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun internalRenameFileTo(srcFile:File, desFile:File):Boolean{
        return if(desFile.exists()){
            false
        } else{
            srcFile.renameTo(desFile)
        }
    }
    fun renameFileTo(context: Context,srcFile:File,desFile:File):Boolean{
        return if(desFile.exists()){
            false
        } else{
            val result = srcFile.renameTo(desFile)
            scanUpdateFile(context,desFile.absolutePath)
            result
        }
    }
    fun renameFileTo(context: Context,srcPath:String,desPath:String):Boolean{
        val srcFile = File(srcPath)
        val desFile = File(desPath)
        return if(desFile.exists()){
            false
        } else{
            val result = srcFile.renameTo(desFile)
            scanUpdateFile(context,desFile.absolutePath)
            result
        }
    }
    fun scanUpdateFile(context: Context?,path: String?){
        if(context==null || path == null) return
        try {
            MediaScannerConnection.scanFile(
                context, arrayOf(path),
                null
            ) { _:String?, _:Uri? ->
//                Log.e("SCAN UPDATE","OK: $pathResult, uri = $uriResult")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Move a file, even across volumes.
     * @param from origin path
     * @param to destination path
     * @param overwrite should we overwrite existing files?
     * @throws IOException
     */
    @Throws(IOException::class)
    @JvmOverloads
    fun moveFileWithExceptions(from: File, to: File, overwrite: Boolean = true) {
        if (!from.exists()) {
            throw FileNotFoundException(String.format("'from' file was not found (%s).", from.toString()))
        }

        if (overwrite && to.exists()) {
            if (!to.delete()) {
                throw IOException(String.format("'to' file was cannot be overwritten (%s).", to.toString()))
            }
        }
        if (from.renameTo(to)) {
            return
        }
        if (to.createNewFile()) {
            var source: FileChannel? = null
            var destination: FileChannel? = null
            try {
                source = FileInputStream(from).channel
                destination = FileOutputStream(to).channel
                destination!!.transferFrom(source, 0, source!!.size())
            } catch (ex: IOException) {
                to.delete()
                throw ex
            } finally {
                source?.close()
                destination?.close()
                from.delete()
            }
        }
        else {
            throw IOException(String.format("'to' file was cannot be created (%s).", to.toString()))
        }
    }

    /**
     * Move a file, even across volumes.
     * Does not throw on failure.
     * @param from origin file
     * @param to destination file
     * @param overwrite should we overwrite existing files?
     * @return true if successful
     */
    @JvmOverloads
    fun moveFile(from: File, to: File, overwrite: Boolean = true): Boolean {
        try {
            moveFileWithExceptions(from, to, overwrite)
            return true
        } catch (ignore: IOException) { }

        return false
    }

    /**
     * Move a file, even across volumes.
     * Does not throw on failure.
     * @param from origin file
     * @param to destination file
     * @param overwrite should we overwrite existing files?
     * @return true if successful
     */
    @JvmOverloads
    fun moveFile(from: String, to: String, overwrite: Boolean = true): Boolean {
        val fromFile = File(from)
        val toFile = File(to)
        return moveFile(fromFile,toFile,overwrite)
    }

    /**
     * Copy a file, even across volumes.
     * Does not throw on failure.
     * @param from origin file
     * @param to destination file
     * @param overwrite should we overwrite existing files?
     * @return true if successful
     */
    @JvmOverloads
    fun copyFile(context: Context,from: File, to: File, overwrite: Boolean = true,onResult:((String,Boolean)->Unit)? = null): Boolean {
        return try {
            copyFileWithExceptions(context,from, to, overwrite,onResult)
            true
        } catch (ignore: IOException) {
            ignore.printStackTrace()
            false
        }
    }

    /**
     * Copy a file, even across volumes.
     * Does not throw on failure.
     * @param from origin path
     * @param to destination path
     * @param overwrite should we overwrite existing files?
     * @return true if successful
     */
    @JvmOverloads
    fun copyFile(context: Context,from: String, to: String,overwrite: Boolean = true,onResult:((String,Boolean)->Unit)? = null):Boolean {
        val fromFile = File(from)
        val toFile = File(to)
        return copyFile(context,fromFile,toFile,overwrite,onResult)
    }

    /**
     * Copy a file, even across volumes.
     * @param from origin file
     * @param to destination file
     * @param overwrite should we overwrite existing files?
     * @throws IOException
     */
    @Throws(IOException::class)
    @JvmOverloads
    fun copyFileWithExceptions(context: Context,from: File, to: File, overwrite: Boolean = true,onResult:((String,Boolean)->Unit)? = null) {
        if (!from.exists()) {
            throw FileNotFoundException(String.format("'from' file was not found (%s).", from.toString()))
        }

        if (overwrite && to.exists()) {
            if (!to.delete()) {
                throw IOException(String.format("'to' file was cannot be overwritten (%s).", to.toString()))
            }
        }
        to.parentFile?.mkdirs()
        val newToFile = if(!overwrite) createFileFrom(to.path) else to
        if (newToFile.createNewFile()) {
            var source: FileChannel? = null
            var destination: FileChannel? = null
            try {
                source = FileInputStream(from).channel
                destination = FileOutputStream(newToFile).channel
                destination!!.transferFrom(source, 0, source!!.size())
                onResult?.invoke(newToFile.absolutePath,true)
            }
            catch (ex: IOException) {
                newToFile.delete()
                onResult?.invoke(newToFile.absolutePath,false)
                throw ex
            } finally {
                source?.close()
                destination?.close()
                scanUpdateFile(context,newToFile.path)
            }
        } else {
            throw IOException(String.format("'to' file was cannot be created (%s).", newToFile.toString()))
        }
    }

    private fun createFileFrom(pathFileFrom:String):File{
        var fileFrom = File(pathFileFrom)
        val parentPath = fileFrom.parent!!
        fileFrom.parentFile?.mkdirs()
        val fileNameOrigin = fileFrom.nameWithoutExtension
        val fileExt = fileFrom.extension
        var version = 0
        while (fileFrom.exists()){
            val newPath = if(version==0) "$parentPath/$fileNameOrigin.$fileExt"
            else "$parentPath/$fileNameOrigin($version).$fileExt"
            version++
            fileFrom = File(newPath)
        }
        return fileFrom
    }
    fun getDuration(file: File): Long? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        val inputStream = FileInputStream(file.absolutePath)
        return try {
            mediaMetadataRetriever.setDataSource(inputStream.fd)
            val durationStr =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLong()
        } catch (e:Exception){
            null
        } finally {
            mediaMetadataRetriever.release()
            inputStream.close()
        }
    }

    fun getSampleRate(path: String): Int {
        val mex = MediaExtractor()
        try {
            mex.setDataSource(path) // the adresss location of the sound on sdcard.
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val mf: MediaFormat = mex.getTrackFormat(0)
        return mf.getInteger(MediaFormat.KEY_SAMPLE_RATE)
    }
}