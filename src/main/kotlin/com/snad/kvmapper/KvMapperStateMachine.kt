package com.snad.kvmapper

import com.snad.kvmapper.arch.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

class KvMapperStateMachine(
    coroutineScope: CoroutineScope,
    private val kvMapper: KvMapper
): StateMachine<State, Action>(initialState = State.initial) {

    init {
        actions
            .map { reduce(it, state.value) }
            .onEach { updateState(it) }
            .launchIn(coroutineScope)
    }

    private fun reduce(action: Action, state: State): State {
        return when(action) {
            is ConvertClicked -> convert(state, action)
            is ErrorDismissed -> state.copy(error = null)
        }
    }

    private fun convert(
        state: State,
        action: ConvertClicked
    ): State {
        val output = kvMapper.convertInput(action.text, action.inputPattern, action.outputPattern)
        val errorMessage = when {
            output == null -> "\$KEY or \$VALUE couldn't be found or are not separated by any symbol."
            output.isBlank() -> "Input doesn't match input pattern."
            else -> null
        }

        return if(errorMessage != null) {
            state.copy(
                error = Error(
                    title = "Parsing error",
                    message = errorMessage
                )
            )
        } else {
            state.copy(output = output!!)
        }
    }
}

