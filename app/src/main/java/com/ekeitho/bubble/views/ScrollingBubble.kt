package com.ekeitho.bubble.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ekeitho.bubble.alphabetItemSize

@Composable
fun ScrollingBubble(
    boxConstraintMaxWidth: Dp,
    bubbleOffsetYFloat: Float,
    currAlphabetScrolledOn: Char,
) {
    val bubbleSize = 96.dp
    Surface(
        shape = CircleShape,
        modifier = Modifier
            .size(bubbleSize)
            .offset(
                x = (boxConstraintMaxWidth - (bubbleSize + alphabetItemSize)),
                y = with(LocalDensity.current) {
                    bubbleOffsetYFloat.toDp() - (bubbleSize / 2)
                },
            ),
        color = MaterialTheme.colorScheme.primary,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currAlphabetScrolledOn.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}