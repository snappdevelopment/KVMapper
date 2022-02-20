
data class State(
    val output: String,
    val error: Error?
) {
    companion object {
        val initial = State(
            output = "",
            error = null
        )
    }
}

data class Error(
    val title: String,
    val message: String
)