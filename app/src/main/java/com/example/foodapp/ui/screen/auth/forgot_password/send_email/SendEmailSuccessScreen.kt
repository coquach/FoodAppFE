package com.example.foodapp.ui.screen.auth.forgot_password.send_email

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.navigation.Auth
import androidx.core.net.toUri
import com.google.android.gms.common.wrappers.Wrappers.packageManager

@Composable
fun SendEmailSuccessScreen(
    navController: NavController,
){
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, androidx.compose.ui.Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(id = R.drawable.send_email),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
        Text(
            text = stringResource(R.string.send_email_success),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.check_email),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, "addresses")
                    putExtra(Intent.EXTRA_SUBJECT, "subject")
                    putExtra(Intent.EXTRA_STREAM, "attachment")
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
               },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)

        ) {
            Text(text = "Kiểm tra email", style = MaterialTheme.typography.bodyMedium)
        }
        Button(
            onClick = {
                navController.navigate(Auth) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.outline, contentColor = MaterialTheme.colorScheme.onPrimary),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 16.dp)
        ) {
            Text(text = "Trở về", style = MaterialTheme.typography.bodyMedium)
        }
    }
}