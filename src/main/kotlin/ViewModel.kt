import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

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

class ViewModel(
    coroutineScope: CoroutineScope
): StateMachine<ViewModelState, Action>(initialState = ViewModelState(output = "")) {

    init {
        actions
            .map { reduce(it, state.value) }
            .onEach { updateState(it) }
            .launchIn(coroutineScope)
    }

    private fun reduce(action: Action, state: ViewModelState): ViewModelState {
        return when(action) {
            is InputChanged -> state.copy(output = action.text)
        }
    }
}

