// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect

@Composable
@Preview
fun App(stateMachine: StateMachine<ViewModelState, Action>) {

    var state by remember { mutableStateOf(ViewModelState.initial) }

    LaunchedEffect(Unit) {
        stateMachine.state.collect {
            state = it
        }
    }

    MaterialTheme {
        Button(onClick = {
            stateMachine.sendAction(InputChanged("Hello"))
        }) {
            Text(state.output)
        }
    }
}

fun main() {

    val job = Job()
    val coroutineScope = CoroutineScope(job)
    val viewModel = ViewModel(coroutineScope)

    application {
        Window(
            title = "KVMapper",
            onCloseRequest = {
                coroutineScope.cancel()
                exitApplication()
            }
        ) {
            App(viewModel)
        }
    }
}
