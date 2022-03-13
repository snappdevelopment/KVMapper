package com.snad.kvmapper

import com.snad.kvmapper.arch.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

class KvMapperStateMachine(
    coroutineScope: CoroutineScope,
    private val kvMapper: KvMapper,
    private val patternPersister: PatternPersister
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
            is LoadSavedPattern -> {
                state.copy(savedPattern = patternPersister.getPattern())
            }
            is SaveCurrentPatternClicked -> {
                if(action.pattern.isNotEmpty()) patternPersister.savePattern(action.pattern)
                state.copy(savedPattern = patternPersister.getPattern())
            }
            is DeletePatternClicked -> {
                patternPersister.deletePattern(action.pattern)
                state.copy(savedPattern = patternPersister.getPattern())
            }
        }
    }

    private fun convert(
        state: State,
        action: ConvertClicked
    ): State {
        val output = kvMapper.convertInput(action.text, action.inputPattern, action.outputPattern)
        val error = when {
            output == null -> Error.ParsingError()
            output.isBlank() -> Error.InputError()
            else -> null
        }

        return if(error != null) {
            state.copy(error = error)
        } else {
            state.copy(output = output!!)
        }
    }
}

