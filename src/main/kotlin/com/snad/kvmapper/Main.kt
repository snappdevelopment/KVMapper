package com.snad.kvmapper

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect

fun main() {

    val job = Job()
    val coroutineScope = CoroutineScope(job)
    val kvMapper: KvMapper = KvMapperImpl()
    val kvMapperStateMachine = KvMapperStateMachine(coroutineScope, kvMapper)

    application {
        Window(
            title = "KVMapper",
            onCloseRequest = {
                coroutineScope.cancel()
                exitApplication()
            }
        ) {
            var state by remember { mutableStateOf(State.initial) }

            LaunchedEffect(Unit) {
                kvMapperStateMachine.state.collect {
                    state = it
                }
            }

            App(state, kvMapperStateMachine::sendAction)
        }
    }
}
