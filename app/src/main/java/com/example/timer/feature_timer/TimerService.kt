package com.example.timer.feature_timer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Binder
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
import com.example.timer.R
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
class TimerService : Service(), RecognitionListener {

    private var startMode: Int = 0            // indicates how to behave if the service is killed
    private var binder: IBinder = LocalBinder()       // interface for clients that bind
    private var allowRebind: Boolean = true   // indicates whether onRebind should be used

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var vibrator: Vibrator
    private lateinit var ringtone: Ringtone

    private val dataClient by lazy { Wearable.getDataClient(this) }

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    var countDownTimer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate() {
        super.onCreate()

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        ringtone = RingtoneManager.getRingtone(applicationContext, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)

        notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.apply {
            // make sure no sound gets played every time the notification is updated (which is every second while the timer is running)
            setSound(
                null,
                AudioAttributes.Builder().build()
            )
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createOnDeviceSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(this)
        }

        recognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName)
    }


    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    private fun notifiedStart() {
        startListeningSafe()

        // Cancel the old CountDownTimer if it exists
        countDownTimer?.cancel()

        countDownTimer = createCountDownTimer().start()

        ClockTimer.timerState.value = TimerState.Running

        startForeground(NOTIFICATION_ID.toInt(), createNotification(this).build())
    }

    private fun start() {
        serviceScope.launch {
            try {
                val request = PutDataMapRequest.create(START_TIMER_SEND).apply {
                    dataMap.putInt(
                        DataLayerListenerService.TIMER_DURATION_KEY,
                        ClockTimer.secondsRemaining.intValue
                    )
                    dataMap.putInt(
                        DataLayerListenerService.START_TIMER_TIME_KEY,
                        System.currentTimeMillis().toInt()
                    )
                }
                    .asPutDataRequest()
                    .setUrgent()

                dataClient.putDataItem(request).await()
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

        ringtone.stop()

        ClockTimer.apply {
            secondsRemaining.intValue = 0
            timerState.value = TimerState.Stopped
            timerDurationForRepeat.intValue = 0
        }

        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun reset() {
        serviceScope.launch {
            try {
                //TODO convert to message api
                val request = PutDataMapRequest.create(RESET_TIMER_SEND).apply {
                    dataMap.putInt(
                        DataLayerListenerService.TIMER_DURATION_KEY,
                        ClockTimer.secondsRemaining.intValue
                    )
                    dataMap.putInt(
                        DataLayerListenerService.START_TIMER_TIME_KEY,
                        System.currentTimeMillis().toInt()
                    )
                }
                    .asPutDataRequest()
                    .setUrgent()

                dataClient.putDataItem(request).await()

                return@launch
            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")
            }
        }

        notifiedReset()
    }

    private fun notifiedPause() {
        countDownTimer?.cancel()
        ClockTimer.timerState.value = TimerState.Paused
        applicationContext.updateNotificationContentText(
            ClockTimer.secondsRemaining.intValue.intTimeToString()
        )
    }

    private fun pause() {
        serviceScope.launch {
            try {
                //TODO convert to message api
                val request = PutDataMapRequest.create(PAUSE_TIMER_SEND).apply {
                    dataMap.putInt(
                        DataLayerListenerService.TIMER_DURATION_KEY,
                        ClockTimer.secondsRemaining.intValue
                    )
                    dataMap.putInt(
                        DataLayerListenerService.START_TIMER_TIME_KEY,
                        System.currentTimeMillis().toInt()
                    )
                }
                    .asPutDataRequest()
                    .setUrgent()

                dataClient.putDataItem(request).await()

                return@launch
            } catch (exception: Exception) {
                Log.d(ContentValues.TAG, "Saving DataItem failed: $exception")
            }
        }

        notifiedPause()
    }

    private fun repeat() {
        ClockTimer.secondsRemaining.intValue = ClockTimer.timerDurationForRepeat.intValue

        start()
    }

    private fun notifiedRepeat() {
        ClockTimer.secondsRemaining.intValue = ClockTimer.timerDurationForRepeat.intValue

        notifiedStart()
    }

    fun createCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(ClockTimer.secondsRemaining.intValue.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ClockTimer.secondsRemaining.intValue -= 1
                applicationContext.updateNotificationContentText(
                    ClockTimer.secondsRemaining.intValue.intTimeToString()
                )
            }

            @RequiresApi(34)
            override fun onFinish() {
                ClockTimer.timerState.value = TimerState.Finished
                startListeningSafe()

                vibrate()

                if( !ClockTimer.muted.value ) ring()

                applicationContext.updateNotificationAlarmFinished()
            }
        }
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

    private fun ring() {
        ringtone.play()
    }

    fun startListeningSafe() {
        if (!::speechRecognizer.isInitialized) return
        speechRecognizer.startListening(recognizerIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.Start.toString() -> start()
            Action.StartListening.toString() -> startListeningSafe()
            Action.NotifiedStart.toString() -> notifiedStart()
            Action.Pause.toString() -> pause()
            Action.NotifiedPause.toString() -> notifiedPause()
            Action.Reset.toString() -> reset()
            Action.NotifiedReset.toString() -> notifiedReset()
            Action.Repeat.toString() -> repeat()
            Action.NotifiedRepeat.toString() -> notifiedRepeat()
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

    /*
    * Start, informs wearable of the start of the timer, then calls start
    * NotifiedStart, starts the timer, sets the timer state to running and starts the foregroundservice
    * pause, informs wearable of the pause of the timer, then calls pause
    * NotifiedPause, stops the timer, sets the timer state to paused
    * reset, informs wearable of the reset of the timer, then calls reset
    * NotifiedReset, stops the timer and vibrator, resets the ClockTimer values and stops the foregroundservice
    * StartListening, starts the speechRecognizer
    * Repeat, set the ClockTimer secondsRemaining to the timerDurationForRepeat and calls start
    * NotifiedRepeat, set the ClockTimer secondsRemaining to the timerDurationForRepeat and calls notifiedStart
    * Stop, calls reset
    * */
    enum class Action {
        Start, NotifiedStart, Pause, NotifiedPause, Reset, NotifiedReset, StartListening, Repeat, NotifiedRepeat
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.i("voicexx", "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.i("voicexx", "onBeginningOfSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {
        Log.i("voicexx", "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        Log.i("voicexx", "onEndOfSpeech")

    }

    override fun onError(error: Int) {
        Log.e("voicexx", "Error:  $error")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResults(results: Bundle?) {
        Log.i(
            "voicexx",
            "onResults ${results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}"
        )

        val recognizedWords =
            results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.split(" ")

        Log.i("voicexx", "recognizedWords $recognizedWords")

        if (recognizedWords.isNullOrEmpty()) {
            startListeningSafe()
            return
        }

        val resetWords: List<String> = listOf(
            getString(R.string.reset),
        )
        if (resetWords.any{ it in recognizedWords }) {
            reset()
        }

        val restartWords: List<String> = listOf(
            getString(R.string.restart),
            getString(R.string.repeat),
        )
        if (restartWords.any{ it in recognizedWords }) {
            if(ClockTimer.timerState.value == TimerState.Finished) {
                repeat()
            }
        }

        recognizedWords.indexOfFirst { it.contains(getString(R.string.second)) }.let { secondIndex ->
            if (secondIndex - 1 < 0) return@let

            recognizedWords[secondIndex - 1].toIntOrNull()?.let {
                addTimeClockTimer(it)
            }
        }

        recognizedWords.indexOfFirst { it.contains(getString(R.string.minute)) }.let { minuteIndex ->
            if (minuteIndex - 1 < 0) return@let

            recognizedWords[minuteIndex - 1].toIntOrNull()?.let {
                addTimeClockTimer(it * 60)
            }
        }


        recognizedWords.indexOfFirst { it.contains(getString(R.string.hour)) }.let { hourIndex ->
            if (hourIndex - 1 < 0) return@let

            recognizedWords[hourIndex - 1].toIntOrNull()?.let {
                addTimeClockTimer(it * 60 * 60)
            }
        }

        if (ClockTimer.timerState.value == TimerState.Running) {
            start()
        }

        val startWords: List<String> = listOf(
            getString(R.string.start),
            getString(R.string.go),
            getString(R.string.begin),
            getString(R.string.starts),
            getString(R.string.resume)
        )
        if ( startWords.any{ it in recognizedWords }
            ) {
            start()
        }

        val pauseWords: List<String> = listOf(
            getString(R.string.pause),
            getString(R.string.stop),
            getString(R.string.end)
        )
        if ( pauseWords.any{ it in recognizedWords }
            ) {
            pause()
        }

        startListeningSafe()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.i(
            "voicexx",
            "onPartialResults ${partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}}"
        )
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.i("voicexx", "onEvent")
    }
}