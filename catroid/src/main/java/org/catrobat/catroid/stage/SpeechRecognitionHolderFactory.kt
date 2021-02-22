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

import android.content.Context
import android.speech.SpeechRecognizer
import com.huawei.hms.api.ConnectionResult
import com.huawei.hms.api.HuaweiApiAvailability

class SpeechRecognitionHolderFactory(
    private val speechRecognitionHolder: SpeechRecognitionHolder,
    private val hmsSpeechRecognitionHolder: HmsSpeechRecognitionHolder,
    private val hmsApiAvailability: HuaweiApiAvailability
) {
    companion object {
        private val dummy = object : SpeechRecognitionHolderInterface {
            override var callback: OnSpeechRecognitionResultCallback = object : OnSpeechRecognitionResultCallback {
                override fun onResult(spokenWords: String) {}
            }
        }
    }

    var instance: SpeechRecognitionHolderInterface = dummy
        private set

    fun isRecognitionAvailable(context: Context): Boolean {
        return when {
            SpeechRecognizer.isRecognitionAvailable(context) -> {
//                instance = hmsSpeechRecognitionHolder
                instance = speechRecognitionHolder
                true
            }
            hmsApiAvailability.isHuaweiMobileServicesAvailable(context) == ConnectionResult.SUCCESS -> {
                instance = hmsSpeechRecognitionHolder
                true
            }
            else -> {
                false
            }
        }
    }
}