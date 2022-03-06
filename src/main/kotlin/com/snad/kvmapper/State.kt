package com.snad.kvmapper

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

sealed class Error {
    abstract val title: String
    abstract val message: String

    data class ParsingError(
        override val title: String = "Parsing Error",
        override val message: String = "\$KEY or \$VALUE couldn't be found or are not separated by any symbol."
    ): Error()

    data class InputError(
        override val title: String = "Input Error",
        override val message: String = "Input doesn't match input pattern."
    ): Error()
}
