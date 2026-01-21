package com.noor.khabarlagbe.restaurant.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SoundManager"
    }
    
    private var mediaPlayer: MediaPlayer? = null
    @Volatile
    private var isPlaying = false
    
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    fun playNewOrderSound() {
        Log.d(TAG, "Playing new order sound")
        
        if (isPlaying) {
            stopSound()
        }
        
        try {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
                setDataSource(context, notificationUri)
                isLooping = false
                setOnPreparedListener { mp ->
                    mp.start()
                    this@SoundManager.isPlaying = true
                }
                setOnCompletionListener {
                    this@SoundManager.isPlaying = false
                    release()
                }
                setOnErrorListener { _, _, _ ->
                    this@SoundManager.isPlaying = false
                    release()
                    true
                }
                prepareAsync()
            }
            
            // Also vibrate
            vibrate()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing sound", e)
            isPlaying = false
        }
    }
    
    fun playOrderUpdateSound() {
        Log.d(TAG, "Playing order update sound")
        
        try {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            
            MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .build()
                )
                setDataSource(context, notificationUri)
                setOnPreparedListener { mp ->
                    mp.start()
                }
                setOnCompletionListener { mp ->
                    mp.release()
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing update sound", e)
        }
    }
    
    fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 200, 500),
                    -1
                )
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 500, 200, 500), -1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error vibrating", e)
        }
    }
    
    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error vibrating", e)
        }
    }
    
    fun stopSound() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
            isPlaying = false
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping sound", e)
        }
    }
    
    fun cancelVibration() {
        try {
            vibrator.cancel()
        } catch (e: Exception) {
            Log.e(TAG, "Error canceling vibration", e)
        }
    }
    
    fun cleanup() {
        stopSound()
        cancelVibration()
    }
}
