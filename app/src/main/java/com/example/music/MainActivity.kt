package com.example.music

import android.annotation.SuppressLint
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.music.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mediaPlayer: MediaPlayer
    var isMute = false
    private val timer = Timer()
    var isUserChangeSlider = false
    var isPlaying = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareMusic()

        binding.btnPlay.setOnClickListener { configureMusic() }
        binding.btnBefore.setOnClickListener { goBeforeMusic() }
        binding.btnNext.setOnClickListener { goAfterMusic() }
        binding.btnVolume.setOnClickListener { configureVolume() }

        binding.sliderMusic.addOnChangeListener { _, value, fromUser ->
            binding.musicStart.text = convertMillisToString(value.toLong())
            isUserChangeSlider = fromUser
        }
        binding.sliderMusic.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                mediaPlayer.seekTo(slider.value.toInt())
                isUserChangeSlider = false
            }

        })
    }

    private fun prepareMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music_file)

        binding.sliderMusic.valueTo = mediaPlayer.duration.toFloat()
        binding.musicEnd.text = convertMillisToString(mediaPlayer.duration.toLong())

        timer.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    if (!isUserChangeSlider){
                        binding.sliderMusic.value = mediaPlayer.currentPosition.toFloat()
                    }

                }
            }

        }, 500, 500)
    }

    private fun configureMusic() {
        if (isPlaying) {
            mediaPlayer.pause()
            binding.btnPlay.setImageResource(R.drawable.ic_play)
            isPlaying = false
        } else {
            mediaPlayer.start()
            binding.btnPlay.setImageResource(R.drawable.ic_pause)
            isPlaying = true
        }
    }

    private fun goBeforeMusic() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition - 15000)
        isUserChangeSlider = false
    }

    private fun goAfterMusic() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition + 15000)
        isUserChangeSlider = false
    }

    @SuppressLint("InlinedApi")
    private fun configureVolume() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        if (isMute){
            audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolume.setImageResource(R.drawable.ic_volume_on)
            isMute = false
        }else{
            audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
            binding.btnVolume.setImageResource(R.drawable.ic_volume_off)
            isMute = true
        }
    }

    private fun convertMillisToString(duration: Long): String {
        val second = (duration / 1000) % 60
        var stringSecond = second.toInt().toString()
        val minute = duration / (1000 * 60) % 60
        var stringMinute = minute.toInt().toString()
        if (second < 10) {
            stringSecond = "0" + second.toInt().toString()
        }
        if (minute < 10) {
            stringMinute = "0" + minute.toInt().toString()
        }
        return "$stringMinute:$stringSecond"
    }

    override fun onDestroy() {
        super.onDestroy()

        timer.cancel()
        mediaPlayer.release()
    }
}