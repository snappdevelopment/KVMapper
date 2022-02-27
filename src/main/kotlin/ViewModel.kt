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
            is ConvertClicked -> convert(state, action)
            is ErrorDismissed -> state.copy(error = null)
        }
    }

    private fun convert(
        state: State,
        action: ConvertClicked
    ): State {
        val output = convertInput(action.text, action.inputPattern, action.outputPattern)
        val errorMessage = when {
            output == null -> "\$KEY or \$VALUE couldn't be found or are not separated by any symbol."
            output.isBlank() -> "Input doesn't match input pattern."
            else -> null
        }

        return if(errorMessage != null) {
            state.copy(
                error = Error(
                    title = "Parsing error",
                    message = errorMessage
                )
            )
        } else {
            state.copy(output = output!!)
        }
    }

    private fun convertInput(
        input: String,
        inputPattern: String,
        outputPattern: String
    ): String? {
        val inputPatternParts = inputPattern.analyse()
        val outputPatternParts = outputPattern.analyse()

        if(inputPatternParts == null || outputPatternParts == null) {
            return null
        }

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

    private fun String.analyse(): Pattern? {
        val keyIndex = indexOf(keySymbol, ignoreCase = true)
        val valueIndex = indexOf(valueSymbol, ignoreCase = true)

        if(keyIndex == - 1 || valueIndex == -1) {
            return null
        }

        return if(keyIndex < valueIndex) {
            val prefix = substring(0, keyIndex)
            val infix = substring(keyIndex + keySymbol.length, valueIndex)
            val suffix = substring(valueIndex + valueSymbol.length, length)

            if(infix.isNotEmpty()) Pattern(Order.KEY_FIRST, prefix, infix, suffix) else null
        } else {
            val prefix = substring(0, valueIndex)
            val infix = substring(valueIndex + valueSymbol.length, keyIndex)
            val suffix = substring(keyIndex + keySymbol.length, length)

            if(infix.isNotEmpty()) Pattern(Order.VALUE_FIRST, prefix, infix, suffix) else null
        }
    }

    private fun String.getKeyAndValue(pattern: Pattern): Pair<String, String> {
        val first = removePrefix(pattern.prefix).substringBefore(pattern.infix)
        val second = removePrefix(pattern.prefix + first + pattern.infix).removeSuffix(pattern.suffix)

        return when(pattern.order) {
            Order.KEY_FIRST -> Pair(first, second)
            Order.VALUE_FIRST -> Pair(second, first)
        }
    }
}

