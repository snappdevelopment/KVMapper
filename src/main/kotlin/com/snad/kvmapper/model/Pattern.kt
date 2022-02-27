package com.snad.kvmapper.model

data class Pattern(
    val order: Order,
    val prefix: String,
    val infix: String,
    val suffix: String
)

enum class Order {
    KEY_FIRST,
    VALUE_FIRST
}