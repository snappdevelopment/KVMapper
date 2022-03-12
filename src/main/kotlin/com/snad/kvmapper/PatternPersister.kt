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
        return file
            .readLines()
            .filter { it.isNotEmpty() }
    }

    override fun savePattern(pattern: String) {
        file.appendText("$pattern\n")
    }

    override fun deletePattern(pattern: String) {
        val filteredLines = file
            .readLines()
            .filter { it != pattern }
            .reduceOrNull { acc, s -> acc + "\n" + s }
            ?: ""
        file.writeText(filteredLines)
    }
}