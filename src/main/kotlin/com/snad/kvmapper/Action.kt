package com.snad.kvmapper

sealed interface Action

object InitSavedPattern: Action

data class ConvertClicked(
    val text: String,
    val inputPattern: String,
    val outputPattern: String
): Action

data class SaveCurrentPatternClicked(
    val pattern: String
): Action

data class DeletePatternClicked(
    val pattern: String
): Action

object ErrorDismissed: Action