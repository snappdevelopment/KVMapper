package com.snad.kvmapper

import java.io.File

interface PatternPersister {
    fun getPattern(): List<String>
    fun savePattern(pattern: String)
    fun deletePattern(pattern: String)
}

class PatternPersisterImpl(
    private val file: File
): PatternPersister {

    override fun getPattern(): List<String> {
        return file.readLines()
    }

    override fun savePattern(pattern: String) {
        file.appendText("\n$pattern")
    }

    override fun deletePattern(pattern: String) {
        val filteredLines = file
            .readLines()
            .filter { it != pattern }
            .reduce { acc, s -> acc + "\n" + s }
        file.writeText(filteredLines)
    }
}