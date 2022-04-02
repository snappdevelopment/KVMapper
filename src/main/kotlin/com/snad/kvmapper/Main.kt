package com.snad.kvmapper

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import java.io.File

private const val RESOURCE_DIR = "compose.application.resources.dir"
private const val PATTERN_FILE = "pattern.txt"

fun main() {

    val job = Job()
    val coroutineScope = CoroutineScope(job)

    val patternFile = File(System.getProperty(RESOURCE_DIR)).resolve(PATTERN_FILE)
    val patternPersister: PatternPersister = PatternPersisterImpl(patternFile)
    val kvMapper: KvMapper = KvMapperImpl()
    val kvMapperStateMachine = KvMapperStateMachine(coroutineScope, kvMapper, patternPersister)

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
