package de.oelkers.firenote.controllers.detail

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.button.MaterialButton

class AudioPlayerButton : MaterialButton {

    private var isPlaying: Boolean = false
    private lateinit var player: MediaPlayer

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun registerListener(getFileName: () -> String) {
        setOnClickListener { playAudio(getFileName()) }
    }

    private fun playAudio(fileName: String) {
        if (isPlaying) {
            player.stop()
            player.release()
            onAudioCompleted()
        } else {
            player = MediaPlayer().apply {
                setDataSource(fileName)
                setOnCompletionListener { onAudioCompleted() }
                prepare()
                start()
            }
            onAudioStarted()
        }
    }

    private fun onAudioStarted() {
        icon = AppCompatResources.getDrawable(context, android.R.drawable.ic_media_pause)
        isPlaying = true
    }

    private fun onAudioCompleted() {
        icon = AppCompatResources.getDrawable(context, android.R.drawable.ic_media_play)
        isPlaying = false
    }
}
