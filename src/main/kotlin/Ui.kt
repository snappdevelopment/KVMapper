import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import arch.StateMachine
import kotlinx.coroutines.flow.collect

@Composable
@Preview
fun App(stateMachine: StateMachine<State, Action>) {

    var state by remember { mutableStateOf(State.initial) }

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