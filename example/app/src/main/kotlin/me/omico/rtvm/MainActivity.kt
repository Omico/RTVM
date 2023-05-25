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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.omico.rtvm.component.AppCard
import me.omico.rtvm.component.CountDownTimer
import me.omico.rtvm.component.HeightSpacer
import me.omico.rtvm.component.Title

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                App()
            }
        }
    }
}

@Composable
fun App(
    viewModel: AppViewModel = viewModel(),
) {
    val viewState by viewModel.state.collectAsState()
    App(
        viewState = viewState,
        addOneSecond = viewModel::addOneSecond,
        reset = viewModel::reset,
        minusOneSecond = viewModel::minusOneSecond,
        regenerate = viewModel::regenerate,
    )
}

@Composable
fun App(
    viewState: AppViewState,
    addOneSecond: () -> Unit,
    reset: () -> Unit,
    minusOneSecond: () -> Unit,
    regenerate: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AppCard {
            Title(text = "PIN Code")
            HeightSpacer()
            CountDownTimer(viewState = viewState)
            HeightSpacer()
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = viewState.pin ?: "Loading...",
            )
            HeightSpacer()
            Button(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = regenerate,
                content = { Text(text = "Regenerate") },
            )
        }
        HeightSpacer()
        AppCard {
            Title(text = "Timer")
            HeightSpacer()
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = viewState.timer.toString(),
                style = MaterialTheme.typography.headlineLarge,
            )
            HeightSpacer()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(onClick = addOneSecond) {
                    Text(text = "+1s")
                }
                Button(onClick = reset) {
                    Text(text = "Reset")
                }
                Button(onClick = minusOneSecond) {
                    Text(text = "-1s")
                }
            }
        }
    }
}
