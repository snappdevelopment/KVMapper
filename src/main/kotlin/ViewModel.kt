import arch.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

private const val keySymbol = "\$KEY"
private const val valueSymbol = "\$VALUE"

class ViewModel(
    coroutineScope: CoroutineScope
): StateMachine<State, Action>(initialState = State.initial) {

    init {
        actions
            .map { reduce(it, state.value) }
            .onEach { updateState(it) }
            .launchIn(coroutineScope)
    }

    private fun reduce(action: Action, state: State): State {
        return when(action) {
            is ConvertClicked -> {
                val output = convertInput(action.text, action.inputPattern, action.outputPattern)
                state.copy(output = output)
            }
        }
    }

    private fun convertInput(
        input: String,
        inputPattern: String,
        outputPattern: String
    ): String {
        val inputPatternParts = inputPattern.analyse()
        val outputPatternParts = outputPattern.analyse()

        return input
            .trim()
            .lines()
            .map {
                val keyValue = it.trim().getKeyAndValue(inputPatternParts)

                outputPatternParts.first +
                keyValue.first +
                outputPatternParts.second +
                keyValue.second +
                outputPatternParts.third
            }
            .reduce { acc, s -> acc + "\n" + s }
    }

    private fun String.analyse(): Triple<String, String, String> {
        val keyIndex = indexOf(keySymbol, ignoreCase = true)
        val valueIndex = indexOf(valueSymbol, ignoreCase = true)

        if(keyIndex == - 1 || valueIndex == -1) {
            throw java.lang.IllegalStateException("\$KEY symbol or \$VALUE symbol not found")
        }

        if(keyIndex > valueIndex) {
            throw java.lang.IllegalStateException("\$KEY symbol has to be before \$VALUE symbol")
        }

        val prefix = substring(0, keyIndex)
        val middle = substring(keyIndex + keySymbol.length, valueIndex)
        val suffix = substring(valueIndex + valueSymbol.length, length)

        return Triple(prefix, middle, suffix)
    }

    private fun String.getKeyAndValue(pattern: Triple<String, String, String>): Pair<String, String> {
        val key = substringAfter(pattern.first).substringBefore(pattern.second)
        val value = substringAfter(pattern.second).substringBefore(pattern.third)
        return Pair(key, value)
    }
}

