import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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
            var state by remember { mutableStateOf(State.initial) }

            LaunchedEffect(Unit) {
                viewModel.state.collect {
                    state = it
                }
            }

            App(state, viewModel::sendAction)
        }
    }
}
