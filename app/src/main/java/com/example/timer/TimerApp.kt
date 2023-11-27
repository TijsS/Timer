package com.example.timer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class TimerApp: Application(), RecognitionListener {
    private lateinit var speechRecognizer: SpeechRecognizer

        override fun onCreate() {
            super.onCreate()
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                Log.d("xxx", "onCreate: SpeechRecognizer is available")
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
                speechRecognizer.setRecognitionListener(this)
            }
            notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

            notificationChannel.apply {
                enableLights(true)
                setSound(null, AudioAttributes.Builder().build())
            }

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

    fun startListening(context: Context) {

        val recognizerIntent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        )

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)

        speechRecognizer.startListening(recognizerIntent)

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

    override fun onResults(results: Bundle?) {
        Log.i("voicexx","onResults ${results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}}")

        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)?.split(" ")

        if(matches.isNullOrEmpty()) {
//            speechRecognizer.startListening(intent)
            return
        }

        matches.indexOfFirst { it.contains("second") }.let {
            if (it == -1 || it-1 < 0 ) return@let

            matches[it-1].toIntOrNull()?.let {

//                runningTimerService.timer = runningTimerService.updateTimer(runningTimerService.timer, it * 1000)
//                if(runningTimerService.counting) runningTimerService.timer?.start()
            }
        }

        matches.indexOfFirst { it.contains("minute") }.let {
            if (it == -1 || it-1 < 0 ) return@let

            matches[it-1].toIntOrNull()?.let {
//                runningTimerService.timer = runningTimerService.updateTimer(runningTimerService.timer, it * 1000 * 60)
//                if(runningTimerService.counting) runningTimerService.timer?.start()
            }
        }


        matches.indexOfFirst { it.contains("hour") }.let {
            if (it == -1 || it-1 < 0 ) return@let

            matches[it-1].toIntOrNull()?.let {
//                runningTimerService.timer = runningTimerService.updateTimer(runningTimerService.timer, it * 1000 * 60 * 60)
//                if(runningTimerService.counting) runningTimerService.timer?.start()
            }
        }


        if(matches.contains("start") || matches.contains("go") || matches.contains("begin") || matches.contains("starts") || matches.contains("resume")){
            Intent(this, TimerService::class.java).also { intent ->
                intent.action = TimerService.Action.Start.toString()
                this.startService(intent)
            }
        }

        if(matches.contains("reset") || matches.contains("restart")){
            Intent(this, TimerService::class.java).also { intent ->
                intent.action = TimerService.Action.Reset.toString()
                this.startService(intent)
            }
        }

        if(matches.contains("pause") || matches.contains("stop") || matches.contains("end")){
            Intent(this, TimerService::class.java).also { intent ->
                intent.action = TimerService.Action.Stop.toString()
                this.startService(intent)
            }
        }

//        speechRecognizer.startListening(intent)
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.i("voicexx","onPartialResults ${partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)}}")

    }
    override fun onEvent(eventType: Int, params: Bundle?) { Log.i("voicexx","onEvent")}
}