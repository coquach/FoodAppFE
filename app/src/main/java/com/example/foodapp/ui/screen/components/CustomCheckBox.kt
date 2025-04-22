package com.example.foodapp.ui.screen.components


import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.foodapp.ui.theme.FoodAppTheme

@Composable
fun CustomCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    uncheckColor: Color = MaterialTheme.colorScheme.outlineVariant,
    checkColor: Color = MaterialTheme.colorScheme.error,
    checkmarkColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    val transition = updateTransition(targetState = checked, label = "CheckboxAnim")
    val boxColor by transition.animateColor(label = "BoxColor") {
        if (it) checkColor else uncheckColor
    }


    Box (
        modifier = modifier
            .width(50.dp)
            .height(216.dp)
            .clip(RoundedCornerShape(16, 0  ,0, 16))
            .background(boxColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Check",
            tint = checkmarkColor,
            modifier = Modifier.size(30.dp)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewCheckbox() {
    FoodAppTheme {
        CustomCheckbox(
            checked = true,
            onCheckedChange = {},

        )
    }
}