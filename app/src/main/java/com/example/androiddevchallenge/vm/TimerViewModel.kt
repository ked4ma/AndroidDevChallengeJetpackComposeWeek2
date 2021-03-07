package com.example.androiddevchallenge.vm

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    // Countdown time
    var time: Int = 6000
    private val _timerTime = MutableLiveData(time)
    val timerTime: LiveData<Int> get() = _timerTime

    private val _currentTime = MutableLiveData<Int>()
    val currentTime: LiveData<Int> get() = _currentTime
    private var timer = createTimer(time)

    private val _timerState = MutableLiveData(TimerState.IDLE)
    val timerState: LiveData<TimerState> get() = _timerState

    fun start() {
        timer.cancel()
        _currentTime.value = time
        viewModelScope.launch(Dispatchers.Default) {
            _timerState.postValue(TimerState.STARTING)
            delay(2000)
            _timerState.postValue(TimerState.RUNNING)
            launch(Dispatchers.Main) {
                timer = createTimer(time).start()
            }
        }
    }

    fun stop() {
        timer.cancel()
        _timerState.value = TimerState.STOPED
    }

    fun setTimer(hour: Int, min: Int, sec: Int) {
        time = (hour * 3600 + min * 60 + sec) * 1000
        _timerTime.value = time
    }

    private fun createTimer(timeMillis: Int): CountDownTimer =
        object : CountDownTimer(timeMillis.toLong(), INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished.toInt()
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _timerState.value = TimerState.FINISHED
            }
        }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    companion object {
        private const val DONE = 0
        const val INTERVAL = 100 // 100ms
    }
}

enum class TimerState {
    IDLE,
    STARTING,
    RUNNING,
    STOPED,
    FINISHED
}