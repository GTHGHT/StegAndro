package io.github.gthght.stegandro.presentation.introduction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.gthght.stegandro.R
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme
import kotlinx.coroutines.launch

@Composable
fun IntroductionRoute(
    modifier: Modifier = Modifier,
    contents: List<IntroContent> = listOf(
        IntroContent(
            title = "Selamat Datang Ke StegAndro!",
            description = "StegAndro memungkinkan Anda menyembunyikan pesan terenkripsi di dalam gambar digital",
            introImage = painterResource(id = R.drawable.intro_screen_one),
            contentDescription = "Halaman Kesatu"
        ),
        IntroContent(
            title = "Menyembunyikan Pesan Dengan Mudah",
            description = "Cukup pilih gambar, masukkan pesan, masukkan kunci, dan StegAndro akan melakukan sisanya!",
            introImage = painterResource(id = R.drawable.intro_screen_two),
            contentDescription = "Halaman Kedua"
        ),
        IntroContent(
            title = "Bagikan Gambar Ke Orang Lain",
            description = "Bagikan gambar yang berisi pesan tersembunyi melalui email, media sosial, atau platform apa pun",
            introImage = painterResource(id = R.drawable.intro_screen_three),
            contentDescription = "Halaman Ketiga"
        )
    ),
    onDone: () -> Unit = {}
) {
    val currentPage = remember {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        contents.size
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState.currentPage) {
        currentPage.intValue = pagerState.currentPage
    }
    Scaffold { innerPadding ->
        Box(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_steg_andro),
                        contentDescription = "Icon",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "StegAndro",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalPager(
                    state = pagerState,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    contents[pageIndex].let {
                        if (pageIndex % 2 == 0) {
                            ImageFirstIntroContent(
                                content = it, modifier = Modifier
                                    .padding(16.dp)
                                    .padding(bottom = 96.dp)
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                            )
                        } else {
                            ImageLastIntroContent(
                                content = it, modifier = Modifier
                                    .padding(16.dp)
                                    .padding(bottom = 96.dp)
                                    .verticalScroll(
                                        rememberScrollState()
                                    )
                            )
                        }
                    }
                }
            }


            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(
                        start = 16.dp,
                        end = 32.dp,
                        bottom = 24.dp,
                        top = 16.dp
                    )
                    .align(Alignment.BottomCenter)
            ) {
                if (currentPage.intValue + 1 < contents.size) {
                    TextButton(
                        onClick = onDone,
                    ) {
                        Text("Skip")
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))
                Box {
                    FilledIconButton(
                        onClick = {
                            if (currentPage.intValue + 1 < contents.size) {
                                scope.launch {
                                    pagerState.animateScrollToPage(currentPage.intValue + 1)
                                }
                            } else {
                                onDone()
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    StepProgressArc(
                        width = 56f,
                        height = 56f,
                        numSteps = contents.size,
                        completedSteps = currentPage.intValue + 1
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun IntroductionPreview() {
    StegAndroTheme {
        IntroductionRoute()
    }
}