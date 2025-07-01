package com.se114.foodapp.ui.screen.feedback.feedback_details

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.example.foodapp.ui.screen.components.AppButton
import com.example.foodapp.ui.screen.components.ErrorModalBottomSheet
import com.example.foodapp.ui.screen.components.HeaderDefaultView
import com.example.foodapp.ui.screen.components.ImageListPicker
import com.example.foodapp.ui.screen.components.Loading
import com.example.foodapp.ui.screen.components.LoadingButton
import com.example.foodapp.ui.screen.components.NoteInput
import com.google.accompanist.permissions.ExperimentalPermissionsApi


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FeedbackDetailsScreen(
    navController: NavController,
    viewModel: FeedbackDetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showErrorSheet by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        viewModel.event.flowWithLifecycle(lifecycleOwner.lifecycle).collect {
            when (it) {
                is FeedbackDetail.Event.BackToAfterFeedback -> {
                    Toast.makeText(
                        context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }

                FeedbackDetail.Event.OnBack -> {
                    navController.popBackStack()
                }

                FeedbackDetail.Event.ShowError -> {
                    showErrorSheet = true
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getFeedback()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HeaderDefaultView(
            text = "",
            onBack = {
                viewModel.onAction(FeedbackDetail.Action.OnBack)
            },


            )
        when (uiState.feedbackState) {
            FeedbackDetail.GetFeedbackState.Loading -> {
                Loading(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
                ) {


//                    AsyncImage(
//                        model = null,
//                        contentDescription = "Food",
//                        modifier = Modifier
//                            .size(100.dp)
//                            .shadow(8.dp, shape = CircleShape)
//                            .clip(CircleShape)
//                            .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
//                        contentScale = ContentScale.Crop,
//                        placeholder = painterResource(id = R.drawable.ic_placeholder),
//                        error = painterResource(id = R.drawable.ic_placeholder)
//                    )


                    Text(
                        text = "Bạn nghĩ gì về món ăn?\n Hãy đánh giá ngay!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = when (uiState.feedback.rating) {

                            5 -> "Tuyệt vời"
                            4 -> "Ngon"
                            3 -> "Ổn"
                            2 -> "Hơi tệ"
                            else -> "Thất vọng"

                        },
                        style = TextStyle(
                            color = when (uiState.feedback.rating) {
                                5 -> Color(0xFF4CAF50)
                                4 -> Color(0xFF8BC34A)
                                3 -> Color(0xFFFFC107)
                                2 -> Color(0xFFFF9800)
                                else -> Color(0xFFF44336)
                            }
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                    StarRatingBar(
                        rating = uiState.feedback.rating,
                        onRatingChanged = {
                            viewModel.onAction(FeedbackDetail.Action.OnRatingChanged(it))
                        },
                    )
                    ImageListPicker(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        imageList = uiState.feedback.images ?: emptyList(),
                        onUpdateImages = {
                            viewModel.onAction(FeedbackDetail.Action.OnImagesChanged(it))
                        }
                    )
                    NoteInput(
                        modifier = Modifier.height(200.dp),
                        note = uiState.feedback.content ?: "",
                        onNoteChange = {
                            viewModel.onAction(FeedbackDetail.Action.OnContentChanged(it))
                        },
                        textHolder = "Nhập đánh giá.."
                    )
                    if (uiState.isUpdating) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppButton(
                                text = "Cập nhật",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.onAction(FeedbackDetail.Action.OnUpdate)
                                },

                                enable = !uiState.isLoading
                            )
                            AppButton(
                                text = "Xóa",
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    viewModel.onAction(FeedbackDetail.Action.OnDelete)
                                },
                                backgroundColor = MaterialTheme.colorScheme.error,
                                enable = !uiState.isLoading
                            )
                        }
                    } else {
                        LoadingButton(
                            onClick = {
                                viewModel.onAction(FeedbackDetail.Action.OnFeedbackClicked)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            text = "Xác nhận",
                            loading = uiState.isLoading
                        )
                    }


                }
            }
        }


    }
    if (showErrorSheet) {
        ErrorModalBottomSheet(
            description = uiState.error.toString(),
            onDismiss = { showErrorSheet = false }
        )
    }

}

@Composable
fun StarRatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxRating: Int = 5,
) {
    Row(modifier = modifier) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Star $i",
                tint = Color(0xFFFFD700), // Vàng gold
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChanged(i) }
                    .padding(2.dp)

            )
        }
    }
}




