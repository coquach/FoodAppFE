package com.example.foodapp.ui.screen.auth

import android.text.Layout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.R
import com.example.foodapp.ui.GroupSocialButtons
import com.example.foodapp.ui.theme.FoodAppTheme

@Composable
fun AuthScreen() {
    val imageSize = remember { mutableStateOf(IntSize.Zero) }
    val brush = Brush.verticalGradient(
        colors = listOf(
            androidx.compose.ui.graphics.Color.Transparent, androidx.compose.ui.graphics.Color.Black
        ),
        startY = imageSize.value.height.toFloat() / 3,
    )
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(painter = painterResource(id = R.drawable.background), contentDescription = null,
            modifier = Modifier.onGloballyPositioned {
                imageSize.value = it.size
            }.alpha(0.5f),
            contentScale = ContentScale.FillBounds)
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush = brush)
        )
        Column(modifier = Modifier.fillMaxWidth().padding(top = 110.dp).padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.welcome),
                color = MaterialTheme.colorScheme.surface,
                fontSize = 50.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier

            )
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.primaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 50.sp,
                modifier = Modifier
            )
            Text(
                text = stringResource(id = R.string.app_description),
                color = MaterialTheme.colorScheme.surface,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 16.dp)

            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GroupSocialButtons()
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = stringResource(id = R.string.sign_with_email), color = Color.White)
            }
            TextButton(onClick = {}) {
                Text(text = stringResource(id = R.string.already_have_account), color = Color.White)
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    FoodAppTheme {
        AuthScreen()
    }
}