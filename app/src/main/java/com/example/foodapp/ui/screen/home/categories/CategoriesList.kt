package com.example.foodapp.ui.screen.home.categories


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodapp.data.models.response.CategoriesResponse
import com.example.foodapp.data.models.response.Category
import com.example.foodapp.ui.theme.FoodAppTheme


@Composable
fun CategoriesList(
    categories: List<Category>,
    onCategorySelected: (Category) -> Unit
){
    LazyRow {
       items(categories) {
           CategoryItem(category = it, onCategorySelected = onCategorySelected)
       }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onCategorySelected: (Category) -> Unit
){
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(45.dp))
            .height(90.dp)
            .width(60.dp)
            .clickable { onCategorySelected(category) }
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(45.dp),
                ambientColor = Color.Gray.copy(alpha = 0.8f),
                spotColor = Color.Gray.copy(alpha = 0.8f)
            )
            .background(color = MaterialTheme.colorScheme.onPrimary)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = null,
            modifier = Modifier
               .size(40.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = MaterialTheme.colorScheme.primaryContainer,
                    spotColor = MaterialTheme.colorScheme.primaryContainer
                )
               .clip(CircleShape),
            contentScale = ContentScale.Inside
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = category.name,
            color = MaterialTheme.colorScheme.onSurface,
            style = TextStyle(fontSize = 10.sp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}

