package com.se114.foodapp.ui.screen.feedback

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.example.foodapp.R
import com.example.foodapp.data.model.Feedback
import com.example.foodapp.ui.screen.components.ExpandableText
import com.example.foodapp.ui.screen.components.LazyPagingSample
import com.example.foodapp.utils.StringUtils


@Composable
fun FeedbackList(
    feedbacks: LazyPagingItems<Feedback>,
    modifier: Modifier = Modifier
) {
        LazyPagingSample(
            modifier = modifier,
            items = feedbacks,
            textNothing = "Không có đánh giá nào cả...",
            iconNothing = Icons.Default.Reviews,
            columns = 1,
            key = {
                it.id
            },
        ) {
            FeedbackItem(it)
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
                        model = feedback.avatar,
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
                            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${feedback.rating}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))

                Column(verticalArrangement = Arrangement.SpaceAround) {
                    Text(
                        text = feedback.displayName,
                        style = MaterialTheme.typography.bodyLarge,

                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if(feedback.updatedAt!= null) "${StringUtils.formatDateTime(feedback.updatedAt)}"
                        else "${StringUtils.formatDateTime(feedback.createdAt)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
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
            val imageUrls = feedback.images?.map { it.url } ?: emptyList()

            if (imageUrls.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(imageUrls) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Feedback Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(18.dp)),

                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_placeholder),
                            error = painterResource(id = R.drawable.ic_placeholder)
                        )
                    }
                }
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


