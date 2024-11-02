package io.github.gthght.stegandro.presentation.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme

@Composable
fun GuideScreen(
    modifier: Modifier = Modifier,
    content: GuideContent
) {
    Column(modifier = modifier) {
        AsyncImage(model = content.imageUrl, contentDescription = null, modifier = Modifier
            .weight(0.7f)
            .align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            content.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.3f),
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
fun GuideScreenSlider(
    modifier: Modifier = Modifier,
    contents: List<GuideContent>
) {
    val pagerState = rememberPagerState {
        contents.size
    }
    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top,
    ) { pageIndex ->
        GuideScreen(content = contents[pageIndex], modifier = modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GuideScreenPreview(){
    StegAndroTheme {
        GuideScreen(content =
            GuideContent(
                "https://raw.githubusercontent.com/GTHGHT/StegAndro_TestResult/refs/heads/main/modified_1200/Carrena%2C Spain.jpg",
                "This Is A Test"
            ))
    }
}