import androidx.compose.runtime.key
import arch.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import model.Order
import model.Pattern

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

                when(outputPatternParts.order) {
                    Order.KEY_FIRST -> {
                        outputPatternParts.prefix + keyValue.first + outputPatternParts.infix + keyValue.second + outputPatternParts.suffix
                    }
                    Order.VALUE_FIRST -> {
                        outputPatternParts.prefix + keyValue.second + outputPatternParts.infix + keyValue.first + outputPatternParts.suffix
                    }
                }
            }
            .reduce { acc, s -> acc + "\n" + s }
    }

    private fun String.analyse(): Pattern {
        val keyIndex = indexOf(keySymbol, ignoreCase = true)
        val valueIndex = indexOf(valueSymbol, ignoreCase = true)

        if(keyIndex == - 1 || valueIndex == -1) {
            throw java.lang.IllegalStateException("\$KEY symbol or \$VALUE symbol not found")
        }

        return if(keyIndex < valueIndex) {
            val prefix = substring(0, keyIndex)
            val infix = substring(keyIndex + keySymbol.length, valueIndex)
            val suffix = substring(valueIndex + valueSymbol.length, length)

            Pattern(Order.KEY_FIRST, prefix, infix, suffix)
        } else {
            val prefix = substring(0, valueIndex)
            val infix = substring(valueIndex + valueSymbol.length, keyIndex)
            val suffix = substring(keyIndex + keySymbol.length, length)

            Pattern(Order.VALUE_FIRST, prefix, infix, suffix)
        }
    }

    private fun String.getKeyAndValue(pattern: Pattern): Pair<String, String> {
        return when(pattern.order) {
            Order.KEY_FIRST -> {
                val key = substringAfter(pattern.prefix).substringBefore(pattern.infix)
                val value = substringAfter(pattern.infix).substringBefore(pattern.suffix)
                Pair(key, value)
            }
            Order.VALUE_FIRST -> {
                val value = substringAfter(pattern.prefix).substringBefore(pattern.infix)
                val key = substringAfter(pattern.infix).substringBefore(pattern.suffix)
                Pair(key, value)
            }
        }
    }
}

