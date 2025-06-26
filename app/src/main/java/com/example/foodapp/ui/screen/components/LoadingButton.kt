package com.example.foodapp.ui.screen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    loading: Boolean,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    padding: PaddingValues = PaddingValues(vertical = 18.dp, horizontal = 24.dp),
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        enabled = enabled && !loading,
        shape = RoundedCornerShape(12.dp),
        contentPadding = padding,
    ) {
        Box {
            AnimatedContent(
                targetState = loading,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                            fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                },
                label = "LoadingButtonContent"
            ) { target ->
                if (target) {
                    CircularProgressIndicator(
                        color = contentColor,
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = text,
                        color = contentColor,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AppButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    shape: Shape = RoundedCornerShape(12.dp) ,
    padding: PaddingValues = PaddingValues(vertical = 18.dp, horizontal = 24.dp),
    enable: Boolean = true
) {
    Button (
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = shape,
        contentPadding = padding,
        enabled = enable
    ) {
        Text(text = text, color = textColor, style = MaterialTheme.typography.bodyMedium)
    }
}