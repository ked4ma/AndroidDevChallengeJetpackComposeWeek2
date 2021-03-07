package com.example.androiddevchallenge.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.PauseCircle
import androidx.compose.material.icons.twotone.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.green
import com.example.androiddevchallenge.ui.theme.red
import com.example.androiddevchallenge.vm.TimerState
import com.example.androiddevchallenge.vm.TimerViewModel

@Composable
fun TimerButton(
    viewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.observeAsState(TimerState.IDLE)
    when (timerState) {
        TimerState.STARTING, TimerState.RUNNING -> {
            val enabled = timerState == TimerState.RUNNING
            IconButton(
                modifier = modifier
                    .padding(8.dp)
                    .size(64.dp),
                onClick = {
                    viewModel.stop()
                },
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.TwoTone.PauseCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = if (enabled) red else Color.LightGray
                )
            }
        }
        else -> {
            val timerTime by viewModel.timerTime.observeAsState(0)
            val enabled = timerTime > 0
            IconButton(
                modifier = modifier
                    .padding(8.dp)
                    .size(64.dp),
                onClick = {
                    viewModel.start()
                },
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.TwoTone.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = if (enabled) green else Color.LightGray
                )
            }
        }
    }
}

@Composable
fun TimerCounter(
    viewModel: TimerViewModel,
    timerState: TimerState,
    timerInt: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(104.dp)) {
        when (timerState) {
            TimerState.STARTING, TimerState.RUNNING -> {
                var rest = timerInt
                val hour = rest / 3600000
                rest %= 3600000
                val min = rest / 60000
                rest %= 60000
                val sec = rest / 1000
                rest %= 1000
                val msec = rest / 10

                Text(
                    text = stringResource(R.string.time_counter_format, hour, min, sec, msec),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> TimerSetting(viewModel = viewModel, modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun TimerSetting(viewModel: TimerViewModel, modifier: Modifier = Modifier) {
    var rest = viewModel.time
    val hour = rest / 3600000
    rest %= 3600000
    val min = rest / 60000
    rest %= 60000
    val sec = rest / 1000
    var hourState by remember(viewModel.time) { mutableStateOf(hour) }
    var minState by remember(viewModel.time) { mutableStateOf(min) }
    var secState by remember(viewModel.time) { mutableStateOf(sec) }
    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body2) {
        Row(modifier = modifier.padding(horizontal = 4.dp)) {
            OutlinedTextField(
                value = stringResource(R.string.time_format, hourState),
                label = { Text(text = stringResource(R.string.hour)) },
                modifier = Modifier.weight(1F),
                onValueChange = { str ->
                    str.toIntOrNull()?.takeIf { it in (0..99) }?.let {
                        hourState = it
                        viewModel.setTimer(it, minState, secState)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                maxLines = 1
            )
            Text(
                text = stringResource(R.string.time_separator),
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterVertically)
            )
            OutlinedTextField(
                value = stringResource(R.string.time_format, minState),
                label = { Text(text = stringResource(R.string.min)) },
                modifier = Modifier.weight(1F),
                onValueChange = { str ->
                    str.toIntOrNull()?.takeIf { it in (0..60) }?.let {
                        minState = it
                        viewModel.setTimer(hourState, it, secState)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1
            )
            Text(
                text = stringResource(R.string.time_separator),
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .align(Alignment.CenterVertically)
            )
            OutlinedTextField(
                value = stringResource(R.string.time_format, secState),
                label = { Text(text = stringResource(R.string.second)) },
                modifier = Modifier.weight(1F),
                onValueChange = { str ->
                    str.toIntOrNull()?.takeIf { it in (0..60) }?.let {
                        secState = it
                        viewModel.setTimer(hourState, minState, it)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
private fun TimerSettingPreview() {
    MyTheme {
        Surface(color = MaterialTheme.colors.background, modifier = Modifier.height(104.dp)) {
            TimerSetting(TimerViewModel())
        }
    }
}
