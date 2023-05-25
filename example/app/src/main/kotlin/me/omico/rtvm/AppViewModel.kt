/*
 * Copyright 2023 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.omico.rtvm

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.nextInt

@RtvmState(scope = "app")
data class AppViewState(
    val countDown: Int = 0,
    val pin: String? = null,
    val timer: Int = 0,
)

fun AppViewModel.addOneSecond(): Unit = updateTimer { it + 1 }

fun AppViewModel.minusOneSecond(): Unit = updateTimer { it - 1 }

fun AppViewModel.reset(): Unit = updateTimer { 0 }

private var pinCodeGenerationJob: Job? = null
fun AppViewModel.regenerate() {
    ensurePinCodeGenerationJobIsCancelled()
    pinCodeGenerationJob = viewModelScope.launch(Dispatchers.Default) {
        while (isActive) {
            (30 downTo 0).forEach {
                delay(1000)
                countDown.value = it
                if (it == 30) pin.value = Random.nextInt(100000..999999).toString()
            }
        }
    }
}

@RtvmInitialFunction
internal fun AppViewModel.initialize() {
    regenerate()
}

@RtvmInitialFunction
internal fun AppViewModel.initialize2() {
}

private fun AppViewModel.updateTimer(updater: (Int) -> Int) {
    timer.value = updater(timer.value)
}

private fun AppViewModel.ensurePinCodeGenerationJobIsCancelled() {
    pinCodeGenerationJob?.let { job ->
        job.cancel()
        countDown.value = 0
        pin.value = null
        pinCodeGenerationJob = null
    }
}
