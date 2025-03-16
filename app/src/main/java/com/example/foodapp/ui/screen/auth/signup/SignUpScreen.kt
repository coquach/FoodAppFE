package com.example.foodapp.ui.screen.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodapp.R
import com.example.foodapp.ui.FoodAppTextField
import com.example.foodapp.ui.GroupSocialButtons
import com.example.foodapp.ui.screen.auth.AuthScreen
import com.example.foodapp.ui.theme.FoodAppTheme

@Composable
fun SignUpScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var showPassword by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 26.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally


        ) {
            Box(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.sign_up),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,

            )
            Spacer(modifier = Modifier.size(20.dp))
            FoodAppTextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(text = stringResource(id = R.string.username))
                },
                modifier = Modifier.fillMaxWidth()
            )
            FoodAppTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
                modifier = Modifier.fillMaxWidth()
            )
            FoodAppTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {

                    Image(
                        painter = if (showPassword) painterResource(id = R.drawable.ic_slash_eye) else painterResource(id = R.drawable.ic_eye),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showPassword = !showPassword }

                    )
                }

            )
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = stringResource(id = R.string.sign_up),
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = stringResource(id = R.string.already_have_account),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {  }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center

            )
            GroupSocialButtons(color = Color.Black)

        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    FoodAppTheme {
        SignUpScreen()
    }
}