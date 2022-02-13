
data class State(
    val output: String
) {
    companion object {
        val initial = State(
            output = ""
        )
    }
}