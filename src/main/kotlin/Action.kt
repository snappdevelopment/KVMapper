
sealed interface Action

data class ConvertClicked(
    val text: String,
    val inputPattern: String,
    val outputPattern: String
): Action

object ErrorDismissed: Action