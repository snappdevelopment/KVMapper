package com.snad.kvmapper

import app.cash.turbine.test
import com.snad.kvmapper.arch.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class KvMapperStateMachineTest {

    private val job = Job()
    private val coroutineScope = CoroutineScope(job)
    private val kvMapper = FakeKvMapper()

    private val underTest: StateMachine<State, Action> = KvMapperStateMachine(
        coroutineScope = coroutineScope,
        kvMapper = kvMapper
    )

    @Test
    fun `Initial state succeeds`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows output`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null),
                actual = awaitItem()
            )

            kvMapper.output = "def: abc"

            underTest.sendAction(
                ConvertClicked(
                    text = "abc: def",
                    inputPattern = "\$KEY: \$VALUE",
                    outputPattern = "\$VALUE: \$KEY"
                )
            )

            assertEquals(
                expected = State(output = "def: abc", error = null),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows error, output is null`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null),
                actual = awaitItem()
            )

            kvMapper.output = null

            underTest.sendAction(
                ConvertClicked(
                    text = "abc: def",
                    inputPattern = "", //input pattern missing
                    outputPattern = "\$VALUE: \$KEY"
                )
            )

            assertEquals(
                expected = State(output = "", error = Error.ParsingError()),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows error, output is blank`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null),
                actual = awaitItem()
            )

            kvMapper.output = ""

            underTest.sendAction(
                ConvertClicked(
                    text = "",
                    inputPattern = "",
                    outputPattern = "\$VALUE: \$KEY"
                )
            )

            assertEquals(
                expected = State(output = "", error = Error.InputError()),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows error, then error is dismissed`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null),
                actual = awaitItem()
            )

            kvMapper.output = ""

            underTest.sendAction(
                ConvertClicked(
                    text = "",
                    inputPattern = "",
                    outputPattern = "\$VALUE: \$KEY"
                )
            )

            assertEquals(
                expected = State(output = "", error = Error.InputError()),
                actual = awaitItem()
            )

            underTest.sendAction(ErrorDismissed)

            assertEquals(
                expected = State(output = "", error = null),
                actual = awaitItem()
            )
        }
    }
}

private class FakeKvMapper: KvMapper {

    var output: String? = null

    override fun convertInput(input: String, inputPattern: String, outputPattern: String): String? {
        return output
    }
}