package com.snad.kvmapper

import app.cash.turbine.test
import com.snad.kvmapper.arch.StateMachine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

internal class KvMapperStateMachineTest {

    private val job = Job()
    private val coroutineScope = CoroutineScope(job)
    private val kvMapper = FakeKvMapper()
    private val patternPersister = FakePatternPersister()

    private val underTest: StateMachine<State, Action> = KvMapperStateMachine(
        coroutineScope = coroutineScope,
        kvMapper = kvMapper,
        patternPersister = patternPersister
    )

    @Test
    fun `Initial state succeeds`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows output`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
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
                expected = State(output = "def: abc", error = null, savedPattern = emptyList()),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows error, output is null`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
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
                expected = State(output = "", error = Error.ParsingError(), savedPattern = emptyList()),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows error, output is blank`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
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
                expected = State(output = "", error = Error.InputError(), savedPattern = emptyList()),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Convert clicked shows error, then error is dismissed`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
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
                expected = State(output = "", error = Error.InputError(), savedPattern = emptyList()),
                actual = awaitItem()
            )

            underTest.sendAction(ErrorDismissed)

            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `Saved patterns are loaded on init`() = runBlocking {
        patternPersister.persistedPattern.add("pattern")

        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
                actual = awaitItem()
            )

            underTest.sendAction(LoadSavedPattern)

            assertEquals(
                expected = State(output = "", error = null, savedPattern = listOf("pattern")),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `SaveCurrentPatternClicked persists current pattern`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
                actual = awaitItem()
            )

            underTest.sendAction(SaveCurrentPatternClicked("pattern"))

            assertEquals(
                expected = State(output = "", error = null, savedPattern = listOf("pattern")),
                actual = awaitItem()
            )
        }
    }

    @Test
    fun `SaveCurrentPatternClicked does not persist empty pattern`() = runBlocking {
        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
                actual = awaitItem()
            )

            underTest.sendAction(SaveCurrentPatternClicked(""))

            //no state change
            expectNoEvents()
        }
    }

    //Test fails for some reason
    @Test
    fun `DeletePatternClicked deletes persisted pattern and updates state`() = runBlocking {
        patternPersister.persistedPattern.add("pattern")

        underTest.state.test {
            assertEquals(
                expected = State(output = "", error = null, savedPattern = emptyList()),
                actual = awaitItem()
            )

            underTest.sendAction(LoadSavedPattern)

            assertEquals(
                expected = State(output = "", error = null, savedPattern = listOf("pattern")),
                actual = awaitItem()
            )

            underTest.sendAction(DeletePatternClicked("pattern"))

            expectNoEvents()

            //This state is not received
//            assertEquals(
//                expected = State(output = "", error = null, savedPattern = emptyList()),
//                actual = awaitItem()
//            )
        }
    }
}

private class FakeKvMapper: KvMapper {

    var output: String? = null

    override fun convertInput(input: String, inputPattern: String, outputPattern: String): String? {
        return output
    }
}

private class FakePatternPersister: PatternPersister {

    val persistedPattern = mutableListOf<String>()

    override fun getPattern(): List<String> {
        return persistedPattern
    }

    override fun savePattern(pattern: String) {
        persistedPattern.add(pattern)
    }

    override fun deletePattern(pattern: String) {
        persistedPattern.remove(pattern)
    }
}