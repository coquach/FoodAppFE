package com.se114.foodapp.ui.screen.setting.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodapp.R
import com.example.foodapp.ui.FoodAppTextField
import com.example.foodapp.ui.HeaderDefaultView
import com.example.foodapp.ui.theme.FoodAppTheme
import com.example.foodapp.utils.ImageUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController

) {

    val showSheetImage = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
            showSheetImage.value = false
            val file = uri?.let { ImageUtils.getFileFromUri(context, it) }
            Log.d("ImageUtil", "File path: ${file?.absolutePath}")

        }

    val isSavable = remember { mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeaderDefaultView(
            text = "Thông tin cá nhân",
            onBack = {

            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())


        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.BottomEnd
            ) {

                Image(
                    painter = painterResource(id = R.drawable.avatar_placeholder),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                )


                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { showSheetImage.value = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Edit Avatar",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

            }
            Spacer(modifier = Modifier.size(20.dp))
            FoodAppTextField(
                value = "",
                onValueChange = {  },
                label = {
                    Text(text = stringResource(id = R.string.full_name))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(10.dp))
            FoodAppTextField(
                value = "",
                onValueChange = {  },
                label = {
                    Text(text = stringResource(id = R.string.username))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(10.dp))
            FoodAppTextField(
                value = "",
                onValueChange = {  },
                label = {
                    Text(text = stringResource(id = R.string.email))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(10.dp))
            FoodAppTextField(
                value = "",
                onValueChange = {  },
                label = {
                    Text(text = stringResource(id = R.string.password))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(10.dp))
            FoodAppTextField(
                value = "",
                onValueChange = {  },
                label = {
                    Text(text = stringResource(id = R.string.phone_number))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(20.dp))
            Button(
                enabled = isSavable.value,
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.save))
            }

        }

    }
    if (showSheetImage.value) {
        ModalBottomSheet(
            onDismissRequest = { showSheetImage.value = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Chọn ảnh", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        launcher.launch("image/*")
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Chọn ảnh từ thư viện", color = MaterialTheme.colorScheme.onPrimary)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showSheetImage.value = false
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text("Trở lại", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FoodAppTheme {
        ProfileScreen(rememberNavController())
    }
}
