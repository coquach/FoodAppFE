package com.se114.foodapp.ui.screen.feedback

import android.net.Uri
import android.widget.RatingBar
import androidx.collection.mutableIntSetOf
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.ui.screen.components.ExpandableText
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.utils.StringUtils


@Composable
fun FeedbackList(
    feedbacks: LazyPagingItems<Feedback>
) {
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyPagingSample(
            items = feedbacks,
            textNothing = "Không có đánh giá nào cả...",
            iconNothing = Icons.Default.Reviews,
            columns = 1,
            key = {
                it.id
            },
            modifier = Modifier.fillMaxSize()
        ) {
            FeedbackItem(it)
        }
    }

}

@Composable
fun FeedbackItem(
    feedback: Feedback
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {

                    AsyncImage(
                        model = null,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(8.dp, shape = CircleShape)
                            .clip(CircleShape),

                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.avatar_placeholder),
                        error = painterResource(id = R.drawable.avatar_placeholder)
                    )

                    Box(
                        modifier = Modifier

                            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFC107))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${feedback.rating}",
                            fontSize = 10.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

                Column(verticalArrangement = Arrangement.SpaceAround) {
                    Text(
                        text = "Qvc",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if(feedback.updatedAt!= null) "${StringUtils.formatDateTime(feedback.updatedAt)}"
                        else "${StringUtils.formatDateTime(feedback.createdAt)}",
                    )
                }

            }
            if (feedback.content != null){
                ExpandableText(
                    text = feedback.content.trimIndent(),
                    minimizedMaxLines = 4,
                    style = MaterialTheme.typography.bodyMedium
                )
            }



        }
    }
}


@Composable
fun ButtonFeedback(
    onClick: () ->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick.invoke()
            }
            .padding(16.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Reviews,
                contentDescription = "Review",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(30.dp)

            )

            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Hãy đánh giá món ăn..."
            )
        }
    }
}


