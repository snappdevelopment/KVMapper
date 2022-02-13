import arch.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

class ViewModel(
    coroutineScope: CoroutineScope
): StateMachine<State, Action>(initialState = State(output = "")) {

    init {
        actions
            .map { reduce(it, state.value) }
            .onEach { updateState(it) }
            .launchIn(coroutineScope)
    }

    private fun reduce(action: Action, state: State): State {
        return when(action) {
            is InputChanged -> state.copy(output = action.text)
        }
    }
}

