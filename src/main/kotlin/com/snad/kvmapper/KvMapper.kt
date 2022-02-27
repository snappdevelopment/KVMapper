package com.snad.kvmapper

import com.snad.kvmapper.model.Order
import com.snad.kvmapper.model.Pattern

interface KvMapper {
    fun convertInput(
        input: String,
        inputPattern: String,
        outputPattern: String
    ): String?
}

class KvMapperImpl: KvMapper {

    private val keySymbol = "\$KEY"
    private val valueSymbol = "\$VALUE"

    override fun convertInput(
        input: String,
        inputPattern: String,
        outputPattern: String
    ): String? {
        val inputPatternParts = inputPattern.analyse()
        val outputPatternParts = outputPattern.analyse()

        if(inputPatternParts == null || outputPatternParts == null) {
            return null
        }

        return input
            .trim()
            .lines()
            .map {
                val keyValue = it.trim().getKeyAndValue(inputPatternParts)

                when(outputPatternParts.order) {
                    Order.KEY_FIRST -> {
                        outputPatternParts.prefix + keyValue.first + outputPatternParts.infix + keyValue.second + outputPatternParts.suffix
                    }
                    Order.VALUE_FIRST -> {
                        outputPatternParts.prefix + keyValue.second + outputPatternParts.infix + keyValue.first + outputPatternParts.suffix
                    }
                }
            }
            .reduce { acc, s -> acc + "\n" + s }
    }

    private fun String.analyse(): Pattern? {
        val keyIndex = indexOf(keySymbol, ignoreCase = true)
        val valueIndex = indexOf(valueSymbol, ignoreCase = true)

        if(keyIndex == - 1 || valueIndex == -1) {
            return null
        }

        return if(keyIndex < valueIndex) {
            val prefix = substring(0, keyIndex)
            val infix = substring(keyIndex + keySymbol.length, valueIndex)
            val suffix = substring(valueIndex + valueSymbol.length, length)

            if(infix.isNotEmpty()) Pattern(Order.KEY_FIRST, prefix, infix, suffix) else null
        } else {
            val prefix = substring(0, valueIndex)
            val infix = substring(valueIndex + valueSymbol.length, keyIndex)
            val suffix = substring(keyIndex + keySymbol.length, length)

            if(infix.isNotEmpty()) Pattern(Order.VALUE_FIRST, prefix, infix, suffix) else null
        }
    }

    private fun String.getKeyAndValue(pattern: Pattern): Pair<String, String> {
        val first = removePrefix(pattern.prefix).substringBefore(pattern.infix)
        val second = removePrefix(pattern.prefix + first + pattern.infix).removeSuffix(pattern.suffix)

        return when(pattern.order) {
            Order.KEY_FIRST -> Pair(first, second)
            Order.VALUE_FIRST -> Pair(second, first)
        }
    }
}