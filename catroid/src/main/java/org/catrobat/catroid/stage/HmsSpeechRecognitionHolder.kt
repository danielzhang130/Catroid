/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.stage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.annotation.RequiresApi
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.asr.MLAsrListener
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer
import org.catrobat.catroid.formulaeditor.SensorHandler

class HmsSpeechRecognitionHolder : SpeechRecognitionHolderInterface {
    private val TAG = this::class.simpleName
    private var speechRecognizer: MLAsrRecognizer? = null
    private lateinit var speechIntent: Intent
    private lateinit var listener: MLAsrListener
    private lateinit var stageActivity: StageActivity

    override lateinit var callback: OnSpeechRecognitionResultCallback

    override fun forceSetLanguage() {
//        speechIntent.putExtra(
//            MLAsrCaptureConstants.LANGUAGE,
//            SensorHandler.getListeningLanguageSensor()
//        )
    }

    override fun initSpeechRecognition(
        stageActivity: StageActivity,
        stageResourceHolder: StageResourceHolder
    ) {
        this.stageActivity = stageActivity
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun startListening() {
//        val audioRecord = AudioRecord.Builder()
//                 .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
//                 .setAudioFormat(
//                     AudioFormat.Builder()
//                         .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                         .setSampleRate(32000)
//                         .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
//                         .build())
//                 .setBufferSizeInBytes(20*1024)
//                 .build()
//        audioRecord.startRecording()
//        stageActivity.handler.postDelayed(
//            Runnable {
//                     audioRecord.stop()
//                audioRecord.release()
//            }, 5000
//        )
        speechRecognizer = MLAsrRecognizer.createAsrRecognizer(stageActivity)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(
                MLAsrCaptureConstants.LANGUAGE,
                SensorHandler.getListeningLanguageSensor()
            )
            .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_ALLINONE)

        listener = object : MLAsrListener {
            override fun onResults(result: Bundle?) {
                Log.w(TAG, "onResults")
                callback.onResult(result?.getString(MLAsrRecognizer.RESULTS_RECOGNIZED).orEmpty())
                destroy()
            }

            override fun onRecognizingResults(result: Bundle?) {
            }

            override fun onError(error: Int, errorMessage: String?) = Unit
            override fun onStartListening() {
                Log.d(TAG, "onStartListening")
            }

            override fun onStartingOfSpeech() {
                Log.d(TAG, "onStartingOfSpeech")
            }

            override fun onVoiceDataReceived(data: ByteArray?, energy: Float, params: Bundle?) =
                Unit

            override fun onState(state: Int, params: Bundle?) {
            }
        }

        speechRecognizer?.setAsrListener(listener)
        speechRecognizer?.startRecognizing(speechIntent)
    }

    override fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
