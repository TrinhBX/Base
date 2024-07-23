package com.tbx.base.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build

object RequestAudioFocusManager {
    var audioManager:AudioManager? = null
    private var audioFocusRequest:AudioFocusRequest? = null
    fun requestAudioFocus(context: Context?, listener:AudioManager.OnAudioFocusChangeListener):Boolean{
        audioManager = (context?.getSystemService(Context.AUDIO_SERVICE) as? AudioManager)?:return false
        val res: Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(listener)
                build()
            }
            audioManager!!.requestAudioFocus(AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                setAudioAttributes(AudioAttributes.Builder().run {
                    setUsage(AudioAttributes.USAGE_MEDIA)
                    setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    build()
                })
                setAcceptsDelayedFocusGain(true)
                setOnAudioFocusChangeListener(listener)
                build()
            })
        } else{
            audioManager!!.requestAudioFocus(
                listener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
        return res == AudioManager.AUDIOFOCUS_GAIN
    }
    fun abandonAudioFocus(listener: AudioManager.OnAudioFocusChangeListener){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            audioFocusRequest?.let {
               audioManager?.abandonAudioFocusRequest(it)
            }
        } else{
            audioManager?.abandonAudioFocus(listener)
        }
    }
}