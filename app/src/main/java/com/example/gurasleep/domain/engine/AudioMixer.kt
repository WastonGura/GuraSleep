package com.example.gurasleep.domain.engine

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 音频混合引擎
 *  - 多路 MediaPlayer 并行播放
 *  - 循环播放
 *  - 音量独立控制 + 渐弱停止
 */
class AudioMixer(private val context: Context) {

    data class Track(
        val id: String,
        val player: MediaPlayer,
        var volume: Float = 0.8f
    )

    private val _tracks = mutableListOf<Track>()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _masterVolume = MutableStateFlow(0.8f)
    val masterVolume: StateFlow<Float> = _masterVolume.asStateFlow()

    /**
     * 添加音轨。rawResId 无效或加载失败时返回 false
     */
    fun addTrack(id: String, rawResId: Int, volume: Float = 0.8f): Boolean {
        if (_tracks.any { it.id == id }) return false

        return try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(
                    context,
                    android.net.Uri.parse("android.resource://${context.packageName}/$rawResId")
                )
                isLooping = true
                setVolume(volume, volume)
                prepare()
            }

            _tracks.add(Track(id, player, volume))
            true
        } catch (e: Exception) {
            // 音频资源不存在或加载失败 — 静默忽略
            false
        }
    }

    fun removeTrack(id: String) {
        val track = _tracks.find { it.id == id } ?: return
        track.player.apply {
            if (isPlaying) stop()
            release()
        }
        _tracks.remove(track)
    }

    fun setTrackVolume(id: String, volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        _tracks.find { it.id == id }?.let {
            it.volume = v
            it.player.setVolume(v, v)
        }
    }

    fun setMasterVolume(volume: Float) {
        val v = volume.coerceIn(0f, 1f)
        _masterVolume.value = v
        _tracks.forEach { it.player.setVolume(it.volume * v, it.volume * v) }
    }

    fun playAll() {
        _tracks.forEach { it.player.start() }
        _isPlaying.value = true
    }

    fun pauseAll() {
        _tracks.forEach { it.player.pause() }
        _isPlaying.value = false
    }

    fun stopAll() {
        _tracks.forEach {
            if (it.player.isPlaying) it.player.stop()
            it.player.reset()
        }
        _tracks.clear()
        _isPlaying.value = false
    }

    /**
     * 渐弱停止：在 durationMs 内将音量从当前值平滑降到 0
     */
    fun fadeOut(durationMs: Long = 5000L) {
        scope.launch {
            val steps = 50
            val stepMs = durationMs / steps
            val initialVolumes = _tracks.map { it.volume }

            for (i in 1..steps) {
                val factor = 1f - i.toFloat() / steps
                _tracks.forEachIndexed { index, track ->
                    val vol = initialVolumes[index] * factor
                    track.player.setVolume(vol, vol)
                }
                delay(stepMs)
            }

            stopAll()
        }
    }

    fun release() {
        scope.cancel()
        _tracks.forEach { it.player.release() }
        _tracks.clear()
        _isPlaying.value = false
    }
}
