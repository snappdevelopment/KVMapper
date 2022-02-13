
data class ViewModelState(
    val output: String
) {
    companion object {
        val initial = ViewModelState(
            output = ""
        )
    }
}