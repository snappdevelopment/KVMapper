import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

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
