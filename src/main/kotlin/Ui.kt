import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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

        Column(modifier = Modifier.fillMaxSize()) {
            var inputTextValue by remember { mutableStateOf(TextFieldValue()) }
            var inputPatternValue by remember { mutableStateOf(TextFieldValue()) }
            var outputPatternValue by remember { mutableStateOf(TextFieldValue()) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1F),
                    value = inputPatternValue,
                    onValueChange = { inputPatternValue = it },
                    placeholder = { Text(text = "Input pattern (e.g. <\$KEY><\$VALUE>)") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedTextField(
                    modifier = Modifier.weight(1F),
                    value = outputPatternValue,
                    onValueChange = { outputPatternValue = it },
                    placeholder = { Text(text = "Output pattern (e.g. \$KEY: \$VALUE)") },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1F),
                    value = inputTextValue,
                    onValueChange = { inputTextValue = it },
                    placeholder = { Text(text = "Input") }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = {
                        stateMachine.sendAction(
                            ConvertClicked(
                                text = inputTextValue.text,
                                inputPattern = inputPatternValue.text,
                                outputPattern = outputPatternValue.text
                            )
                        )
                    },
                ) {
                    Text(text = "Convert")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1F),
                    value = state.output,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text(text = "Output") }
                )
            }
        }
    }
}