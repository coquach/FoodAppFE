package com.example.foodapp.ui.screen.setting.contact

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.foodapp.R
import com.example.foodapp.ui.screen.components.HeaderDefaultView

@Composable
fun ContactScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeaderDefaultView(
            onBack = { navController.navigateUp() },
            text = "Liên hệ"
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentScale = ContentScale.Crop,

                    )


            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:0377929994".toUri()
                        }
                        context.startActivity(intent)
                    }
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(12.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "0377 929 994", style = MaterialTheme.typography.titleLarge)
            }

            // Email
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:23520189@gm.uit.edu.vn".toUri()
                        }
                        context.startActivity(intent)
                    }
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(12.dp)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "23520189@gm.uit.edu.vn", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
