package com.example.foodapp.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenContainer(
    applyStatusBarInset: Boolean = true,
    content: @Composable () -> Unit
) {
    val finalModifier = if (applyStatusBarInset) {
        Modifier
            .fillMaxSize()
            .padding(
                top = 40.dp,
                bottom = 20.dp
            )
    } else {
        Modifier
            .fillMaxSize()
    }
    Box(modifier = finalModifier) {
        content()
    }
}