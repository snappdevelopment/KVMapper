package com.snad.kvmapper

import org.junit.Test
import kotlin.test.assertEquals

internal class KvMapperTest {

    private val underTest: KvMapper = KvMapperImpl()

    private val defaultInput = "abc: def"
    private val defaultInputPattern = "\$KEY: \$VALUE"
    private val defaultOutputPattern = "\$VALUE: \$KEY"

    //Success

    @Test
    fun `success, prefix not empty, infix not empty, suffix not empty`() {
        val output = underTest.convertInput(
            input = "<abc: def>",
            inputPattern = "<\$KEY: \$VALUE>",
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    @Test
    fun `success, prefix empty, infix not empty, suffix empty`() {
        val output = underTest.convertInput(
            input = defaultInput,
            inputPattern = defaultInputPattern,
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    @Test
    fun `success, prefix empty, infix not empty, suffix not empty`() {
        val output = underTest.convertInput(
            input = "abc: def>",
            inputPattern = "\$KEY: \$VALUE>",
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    @Test
    fun `success, prefix not empty, infix not empty, suffix empty`() {
        val output = underTest.convertInput(
            input = "<abc: def",
            inputPattern = "<\$KEY: \$VALUE",
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    @Test
    fun `success, prefix whitespace, infix not empty, suffix not empty`() {
        val output = underTest.convertInput(
            input = " abc: def>",
            inputPattern = " \$KEY: \$VALUE>",
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    @Test
    fun `success, prefix not empty, infix not empty, suffix whitespace`() {
        val output = underTest.convertInput(
            input = "<abc: def ",
            inputPattern = "<\$KEY: \$VALUE ",
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    @Test
    fun `success, prefix whitespace, infix not empty, suffix whitespace`() {
        val output = underTest.convertInput(
            input = " abc: def ",
            inputPattern = " \$KEY: \$VALUE ",
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    @Test
    fun `success, prefix whitespace, infix whitespace, suffix whitespace`() {
        val output = underTest.convertInput(
            input = " abc def ",
            inputPattern = " \$KEY \$VALUE ",
            outputPattern = defaultOutputPattern
        )

        assertEquals("def: abc", output)
    }

    //Failure

    @Test
    fun `failure, input pattern misses KEY`() {
        val output = underTest.convertInput(
            input = defaultInput,
            inputPattern = "\$VALUE",
            outputPattern = defaultOutputPattern
        )

        assertEquals(null, output)
    }

    @Test
    fun `failure, input pattern misses VALUE`() {
        val output = underTest.convertInput(
            input = defaultInput,
            inputPattern = "\$KEY",
            outputPattern = defaultOutputPattern
        )

        assertEquals(null, output)
    }

    @Test
    fun `failure, output pattern misses KEY`() {
        val output = underTest.convertInput(
            input = defaultInput,
            inputPattern = defaultInputPattern,
            outputPattern = "\$VALUE"
        )

        assertEquals(null, output)
    }

    @Test
    fun `failure, output pattern misses VALUE`() {
        val output = underTest.convertInput(
            input = defaultInput,
            inputPattern = defaultInputPattern,
            outputPattern = "\$KEY"
        )

        assertEquals(null, output)
    }

    @Test
    fun `failure, input pattern misses infix`() {
        val output = underTest.convertInput(
            input = defaultInput,
            inputPattern = "\$KEY\$VALUE",
            outputPattern = defaultOutputPattern
        )

        assertEquals(null, output)
    }

    @Test
    fun `failure, output pattern misses infix`() {
        val output = underTest.convertInput(
            input = defaultInput,
            inputPattern = defaultInputPattern,
            outputPattern = "\$KEY\$VALUE"
        )

        assertEquals(null, output)
    }
}