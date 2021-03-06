/*
 * Copyright 2021 The Android Open Source Project
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
package com.example.androiddevchallenge.ui.view

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.vm.TimerState
import com.example.androiddevchallenge.vm.TimerViewModel
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun TimerView(
    modifier: Modifier = Modifier,
    timerViewModel: TimerViewModel = viewModel()
) {
    val timerState by timerViewModel.timerState.observeAsState(TimerState.IDLE)
    val time = timerViewModel.currentTime.observeAsState(0)
    val timerIntState = remember(timerState) {
        AnimationState(Int.VectorConverter, timerViewModel.time)
    }
    // update timer value
    if (timerState == TimerState.RUNNING) {
        LaunchedEffect(time.value) {
            timerIntState.animateTo(
                time.value,
                animationSpec = tween(
                    durationMillis = TimerViewModel.INTERVAL,
                    easing = LinearEasing
                )
            )
        }
    }
    val timerInt by remember(timerState) {
        if (timerState == TimerState.RUNNING) timerIntState else time
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
            TimerPortrait(
                viewModel = timerViewModel,
                timerState = timerState,
                timerInt = timerInt
            )
        } else {
            TimerLandscape(
                viewModel = timerViewModel,
                timerState = timerState,
                timerInt = timerInt
            )
        }
        if (timerState == TimerState.STARTING) {
            val readySize = remember { Animatable(60F) }
            LaunchedEffect(20F) {
                readySize.animateTo(32F)
            }
            Text(
                text = stringResource(R.string.ready),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.subtitle1,
                fontSize = readySize.value.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TimerPortrait(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel,
    timerState: TimerState,
    timerInt: Int
) {
    Column(modifier.fillMaxSize()) {
        TimerCounter(viewModel, timerState, timerInt, Modifier.fillMaxWidth())
        Hourglass(
            timerState, timerInt, viewModel.time,
            Modifier
                .weight(1F)
                .align(Alignment.CenterHorizontally)
        )
        TimerButton(
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun TimerLandscape(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel,
    timerState: TimerState,
    timerInt: Int
) {
    Row(modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(5F)) {
            TimerCounter(
                viewModel,
                timerState,
                timerInt,
                Modifier
                    .fillMaxWidth()
                    .weight(1F)
            )
            TimerButton(
                viewModel = viewModel,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .weight(1F),
            )
        }
        Hourglass(
            timerState, timerInt, viewModel.time,
            Modifier
                .weight(3F)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
    viewModel: TimerViewModel
) {
    val timerState by viewModel.timerState.observeAsState(TimerState.IDLE)
    val time = viewModel.currentTime.observeAsState(0)
    val timerIntState = remember(timerState) {
        AnimationState(Int.VectorConverter, viewModel.time)
    }
    // update timer value
    if (timerState == TimerState.RUNNING) {
        LaunchedEffect(time.value) {
            timerIntState.animateTo(
                time.value,
                animationSpec = tween(
                    durationMillis = TimerViewModel.INTERVAL,
                    easing = LinearEasing
                )
            )
        }
    }
    val timerInt by remember(timerState) {
        if (timerState == TimerState.RUNNING) timerIntState else time
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column {
            TimerCounter(viewModel, timerState, timerInt, Modifier.fillMaxWidth())
            Hourglass(
                timerState, timerInt, viewModel.time,
                Modifier
                    .weight(1F)
                    .align(Alignment.CenterHorizontally)
            )
        }
        if (timerState == TimerState.STARTING) {
            val readySize = remember { Animatable(60F) }
            LaunchedEffect(20F) {
                readySize.animateTo(32F)
            }
            Text(
                text = stringResource(R.string.ready),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.subtitle1,
                fontSize = readySize.value.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun Hourglass(
    timerState: TimerState,
    timerInt: Int,
    totalTime: Int,
    modifier: Modifier = Modifier,
) {
    val rotate = remember(timerState) { Animatable(0F) }
    if (timerState == TimerState.STARTING) {
        LaunchedEffect(180F) {
            rotate.animateTo(
                180F,
                animationSpec = tween(
                    durationMillis = 1500
                )
            )
        }
    }
    ConstraintLayout(
        modifier = modifier
            .aspectRatio(
                1F,
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
            )
            .rotate(
                if (timerState == TimerState.STARTING) rotate.value else 0F
            )
            .padding(4.dp)
    ) {
        val centerGuide = createGuidelineFromTop(0.45F)
        val bottomGuide = createGuidelineFromBottom(0.11F)
        val (hourglass, remainSand, passedSand) = createRefs()
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(hourglass) {
                    top.linkTo(parent.top)
                },
            painter = painterResource(id = R.drawable.hourglass_two_tone),
            contentDescription = null
        )
        val progress = if (timerState == TimerState.RUNNING) {
            (totalTime - timerInt).toFloat() / totalTime
        } else 1F
        Image(
            modifier = Modifier.constrainAs(remainSand) {
                bottom.linkTo(centerGuide)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.percent(0.48F * (1 - progress))
                height = Dimension.percent(0.34F * (1 - progress))
            },
            contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.sand_down),
            contentDescription = null
        )
        Image(
            modifier = Modifier.constrainAs(passedSand) {
                bottom.linkTo(bottomGuide)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.percent(0.48F * progress)
                height = Dimension.percent(0.34F * progress)
            },
            contentScale = ContentScale.FillBounds,
            painter = painterResource(id = R.drawable.sand),
            contentDescription = null
        )
    }
}
