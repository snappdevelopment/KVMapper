package arch

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow

abstract class StateMachine<STATE, ACTION>(
    initialState: STATE
) {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _actions = Channel<ACTION>()
    protected val actions = _actions.consumeAsFlow()

    protected suspend fun updateState(state: STATE) {
        _state.emit(state)
    }

    fun sendAction(action: ACTION) {
        _actions.trySend(action)
    }
}