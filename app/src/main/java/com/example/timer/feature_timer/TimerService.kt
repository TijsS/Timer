package com.example.timer.feature_timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.timer.feature_notification.CHANNEL_ID
import com.example.timer.feature_notification.CHANNEL_NAME
import com.example.timer.feature_notification.NOTIFICATION_ID
import com.example.timer.feature_notification.createNotification
import com.example.timer.feature_notification.notificationChannel
import com.example.timer.feature_notification.notificationManager
import com.example.timer.feature_notification.updateNotificationAlarmFinished
import com.example.timer.feature_notification.updateNotificationContentText
import com.example.timer.feature_wearable.DataLayerListenerService
import com.example.timer.feature_wearable.DataLayerListenerService.Companion.PAUSE_TIMER_SEND
import com.example.timer.feature_wearable.DataLayerListenerService.Companion.RESET_TIMER_SEND
import com.example.timer.feature_wearable.DataLayerListenerService.Companion.START_TIMER_SEND
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.S)
class TimerService: Service(), RecognitionListener {

    private var startMode: Int = 0             // indicates how to behave if the service is killed
    private var binder: IBinder? = null        // interface for clients that bind
    private var allowRebind: Boolean = true   // indicates whether onRebind should be used

    private lateinit var speechRecognizer: SpeechRecognizer

    private lateinit var vibrator: Vibrator

    private val dataClient by lazy { Wearable.getDataClient(this) }

    private var countDownTimer: CountDownTimer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    private val recognizerIntent = Intent(
        RecognizerIntent.ACTION_RECOGNIZE_SPEECH
    )


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d("xxx", "create vibratormanager ")
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            Log.d("xxx", "create vibrator  ")
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.apply {
            enableLights(true)
            setSound(null, AudioAttributes.Builder().build())
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createOnDeviceSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(this)
        }

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
    }

    private fun notifiedStart() {
        startListeningSafe()

        countDownTimer?.cancel() // Cancel any existing timers

        countDownTimer = object : CountDownTimer(ClockTimer.timeRemaining.intValue.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ClockTimer.timeRemaining.intValue -= 1
                applicationContext.updateNotificationContentText( 2, ClockTimer.timeRemaining.intValue.intTimeToString() )
            }

            @RequiresApi(34)
            override fun onFinish() {
                ClockTimer.timerState.value = TimerState.Finished
                startListeningSafe()

                vibrate()

                applicationContext.updateNotificationAlarmFinished( 2 )

            }
        }.start()

        ClockTimer.timerState.value = TimerState.Running

        startForeground( NOTIFICATION_ID.toInt(), createNotification(this).build() )
    }

    private fun start () {
        serviceScope.launch {
            try {
                val request = PutDataMapRequest.create(START_TIMER_SEND).apply {
                    dataMap.putInt(DataLayerListenerService.TIMER_DURATION_KEY, ClockTimer.timeRemaining.intValue)
                    dataMap.putInt(DataLayerListenerService.START_TIMER_TIME_KEY, System.currentTimeMillis().toInt() )
                }
                    .asPutDataRequest()
                    .setUrgent()

                val response = dataClient.putDataItem(request).await()
                Log.d("xxx", "reset: $response")

                return@launch

            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")

            }
        }
        notifiedStart()
    }

    private fun notifiedReset() {
        countDownTimer?.cancel()

        vibrator.cancel()

        ClockTimer.apply{
            timeRemaining.intValue = 0
            timerState.value = TimerState.Stopped
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun reset(){
        serviceScope.launch {
            try {
                //TODO convert to message api
                val request = PutDataMapRequest.create(RESET_TIMER_SEND).apply {
                    dataMap.putInt(DataLayerListenerService.TIMER_DURATION_KEY, ClockTimer.timeRemaining.intValue)
                    dataMap.putInt(DataLayerListenerService.START_TIMER_TIME_KEY, System.currentTimeMillis().toInt() )
                }
                    .asPutDataRequest()
                    .setUrgent()

                val response = dataClient.putDataItem(request).await()
                Log.d("xxx", "reset: $response")

                return@launch
            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")
            }
        }

        notifiedReset()
    }

    private fun notifiedPause(){
        countDownTimer?.cancel()
        ClockTimer.timerState.value = TimerState.Paused
        applicationContext.updateNotificationContentText( 2, ClockTimer.timeRemaining.intValue.intTimeToString() )
    }

    private fun pause() {
        serviceScope.launch {
            try {
                //TODO convert to message api
                val request = PutDataMapRequest.create(PAUSE_TIMER_SEND).apply {
                    dataMap.putInt(DataLayerListenerService.TIMER_DURATION_KEY, ClockTimer.timeRemaining.intValue)
                    dataMap.putInt(DataLayerListenerService.START_TIMER_TIME_KEY, System.currentTimeMillis().toInt() )
                }
                    .asPutDataRequest()
                    .setUrgent()

                val response = dataClient.putDataItem(request).await()
                Log.d("xxx", "reset: $response")

                return@launch
            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")
            }
        }

        notifiedPause()
    }

    private fun vibrate() {
        vibrator.vibrate(
            VibrationEffect.createWaveform(
                longArrayOf(0, 200, 600, 500),
                intArrayOf(0, 255, 55, 0),
                1
            )
        )
    }

    private fun startListeningSafe() {
        if (!::speechRecognizer.isInitialized ) return
        speechRecognizer.startListening(recognizerIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Action.Start.toString() -> start()
            Action.StartListening.toString() -> startListeningSafe()
            Action.NotifiedStart.toString() -> notifiedStart()
            Action.Pause.toString() -> pause()
            Action.NotifiedPause.toString() -> notifiedPause()
            Action.Reset.toString() -> reset()
            Action.NotifiedReset.toString() -> notifiedReset()
            Action.Stop.toString() -> {
                reset()
            }
        }

        return startMode
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }
    override fun onUnbind(intent: Intent): Boolean {
        return allowRebind
    }
    override fun onRebind(intent: Intent) {
    }

    enum class Action {
        Start, NotifiedStart, Pause, NotifiedPause, Reset, NotifiedReset, Stop, StartListening
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.i("voicexx","onReadyForSpeech")}
    override fun onBeginningOfSpeech() {
        Log.i("voicexx","onBeginningOfSpeech")}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {
        Log.i("voicexx","onBufferReceived")}
    override fun onEndOfSpeech() {
        Log.i("voicexx","onEndOfSpeech")

    }
    override fun onError(error: Int) {
        val errorMsg: String
        when (error) {
            SpeechRecognizer.ERROR_AUDIO -> {
                errorMsg = "Audio recording error"

            }
            SpeechRecognizer.ERROR_CLIENT -> {
                errorMsg = "Unknown client side error"
//                speechRecognizer.startListening(intent)
            }

            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                errorMsg = "Insufficient permissions"
            }

            SpeechRecognizer.ERROR_NETWORK -> {
                errorMsg = "Network related error"
            }

            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                errorMsg = "Network operation timed out"
//                speechRecognizer.startListening(intent)
            }

            SpeechRecognizer.ERROR_NO_MATCH -> {
                errorMsg = "No recognition result matched"
//                speechRecognizer.startListening(intent)
            }

            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                errorMsg = "RecognitionService busy"
            }

            SpeechRecognizer.ERROR_SERVER -> {
                errorMsg = "Server sends error status"
            }

            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                errorMsg = "No speech input"
//                speechRecognizer.startListening(intent)
            }

            else -> errorMsg = ""
        }

        Log.e("voicexx", "Error:  $errorMsg")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResults(results: Bundle?) {
        Log.i("voicexx","onResults ${results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}")

        val recognizedWords = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.split(" ")

        Log.i("voicexx","recognizedWords $recognizedWords")

        if(recognizedWords.isNullOrEmpty()) {
            startListeningSafe()
            return
        }

        if(recognizedWords.contains("reset") || recognizedWords.contains("restart")){
            reset()
        }

        recognizedWords.indexOfFirst { it.contains("second") }.let { secondIndex ->
            if (secondIndex-1 < 0 ) return@let

            recognizedWords[secondIndex-1].toIntOrNull()?.let {
                ClockTimer.timeRemaining.intValue += it
            }
        }

        recognizedWords.indexOfFirst { it.contains("minute") }.let { minuteIndex ->
            if (minuteIndex-1 < 0 ) return@let

            recognizedWords[minuteIndex-1].toIntOrNull()?.let {
                ClockTimer.timeRemaining.intValue += it * 60
            }
        }

        recognizedWords.indexOfFirst { it.contains("hour") }.let { hourIndex ->
            if (hourIndex-1 < 0 ) return@let

            recognizedWords[hourIndex-1].toIntOrNull()?.let {
                ClockTimer.timeRemaining.intValue += it * 60 * 60
            }
        }

        if (ClockTimer.timerState.value == TimerState.Running) {
            //start timer with updated time
            start()
        }

        if(recognizedWords.contains("start") || recognizedWords.contains("go") || recognizedWords.contains("begin") || recognizedWords.contains("starts") || recognizedWords.contains("resume")){
            start()
        }


        if(recognizedWords.contains("pause") || recognizedWords.contains("stop") || recognizedWords.contains("end")){
            pause()
        }

        startListeningSafe()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.i("voicexx","onPartialResults ${partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}}")
    }
    override fun onEvent(eventType: Int, params: Bundle?) { Log.i("voicexx","onEvent")}
}