package com.snad.kvmapper

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val activeColor = Color(0xFF007AFF)
private val inactiveColor = Color(0xFF8E8E93)

@Composable
@Preview
fun App(
    state: State,
    sendAction: (Action) -> Unit
) {
    MaterialTheme {
        Box {
            Content(state, sendAction)

            if(state.error != null) {
                Error(
                    title = state.error.title,
                    message = state.error.message,
                    sendAction = sendAction
                )
            }
        }
    }
}

@Composable
private fun Content(
    state: State,
    sendAction: (Action) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        var inputTextValue by remember { mutableStateOf(TextFieldValue()) }
        var inputPatternValue by remember { mutableStateOf(TextFieldValue()) }
        var outputPatternValue by remember { mutableStateOf(TextFieldValue()) }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            MacTextField(
                modifier = Modifier.weight(1F),
                placeholder = "Input pattern (e.g. <\$KEY><\$VALUE>)",
                value = inputPatternValue,
                onValueChanged = { inputPatternValue = it },
                singleLine = true
            )

            Spacer(modifier = Modifier.width(16.dp))

            MacTextField(
                modifier = Modifier.weight(1F),
                placeholder = "Output pattern (e.g. \$KEY: \$VALUE)",
                value = outputPatternValue,
                onValueChanged = { outputPatternValue = it },
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 16.dp)
        ) {

            MacTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F),
                placeholder = "Input",
                value = inputTextValue,
                onValueChanged = { inputTextValue = it },
                singleLine = false
            )

            Spacer(modifier = Modifier.width(16.dp))

            MacTextField(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F),
                placeholder = "Output",
                value = TextFieldValue(text = state.output),
                onValueChanged = {},
                singleLine = false
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        MacButton(
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp, end = 16.dp),
            text = "Convert",
            onClick = {
                sendAction(
                    ConvertClicked(
                        text = inputTextValue.text,
                        inputPattern = inputPatternValue.text,
                        outputPattern = outputPatternValue.text
                    )
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Error(
    title: String,
    message: String,
    sendAction: (Action) -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {}
            )
            .size(width = 300.dp, height = 200.dp),
        title = {
           Text(text = title)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            MacButton(
                modifier = Modifier.padding(16.dp),
                text = "OK",
                onClick = { sendAction(ErrorDismissed) },
            )
        },
        onDismissRequest = { sendAction(ErrorDismissed) }
    )
}

@Composable
private fun MacTextField(
    modifier: Modifier,
    placeholder: String,
    value: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit,
    singleLine: Boolean
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChanged,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 12.sp
            )
        },
        singleLine = singleLine,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = activeColor,
            unfocusedBorderColor = inactiveColor,
            unfocusedLabelColor = inactiveColor,
            focusedLabelColor = inactiveColor
        )
    )
}

@Composable
private fun MacButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            modifier = Modifier
                .defaultMinSize(minWidth = 157.dp, minHeight = 28.dp)
                .background(color = activeColor, shape = RoundedCornerShape(4.dp))
                .padding(vertical = 6.dp, horizontal = 10.dp),
            text = text,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.White,
        )
    }
}