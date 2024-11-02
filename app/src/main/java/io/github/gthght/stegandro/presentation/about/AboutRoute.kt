package io.github.gthght.stegandro.presentation.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.gthght.stegandro.R
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {}
) {
    val tabTitles = listOf(
        "Pengembang",
        "Apresiasi",
        "Sumber"
    )
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }
    val pagerState = rememberPagerState {
        tabTitles.size
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }
    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(text = "Tentang", style = MaterialTheme.typography.titleMedium)
            },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali Ke Beranda",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, item ->
                    Tab(selected = index == selectedTabIndex, onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        selectedTabIndex = index

                    }, text = {
                        Text(text = item)
                    })
                }
            }
            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> AboutPageOne()
                    1 -> AboutPageTwo()
                    2 -> AboutPageThree()
                    else -> {
                        Text(
                            "Page Not Found",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutPageOne(modifier: Modifier = Modifier) {
    val localContext = LocalContext.current
    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        Box(modifier) {
            OutlinedCard(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(108.dp))
                Text(
                    "Gilang Raditya",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    "sol_lucet@student.unmul.ac.id",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    "Hai, Salam Kenal! Saya mahasiswa " +
                            "dari Universitas Mulawarman. Ini adalah " +
                            "aplikasi hasil dari penelitian saya yang " +
                            "berjudul “Steganografi Untuk Pengamanan " +
                            "Pada Gambar Berbasis Discrete Cosine Transform”" +
                            ". Semoga bermanfaat untuk Anda.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_instagram),
                        contentDescription = "Instagram Pengembang",
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/gt_hght/"))
                                localContext.startActivity(intent)
                            }
                    )
                    Spacer(modifier = Modifier.size(36.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_linkedin),
                        contentDescription = "LinkedIn Pengembang",
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/gilang-raditya-595575256/"))
                                localContext.startActivity(intent)
                            }
                    )
                    Spacer(modifier = Modifier.size(36.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_github),
                        contentDescription = "Github Pengembang",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GTHGHT"))
                                localContext.startActivity(intent)
                            }
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
            }
            Image(
                painter = painterResource(id = R.drawable.img_author),
                contentDescription = "Foto Pengembang",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-72).dp)
                    .fillMaxWidth(0.5f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun AboutPageTwo(modifier: Modifier = Modifier) {
    val credits: List<Pair<String, String>> = listOf(
        Pair("Bapak Medi Taruk, M.Cs.", "Dosen Pembimbing I"),
        Pair("Bapak Prof. Dr. Ir. Hamdani, S.T., M.Cs., IPM.", "Dosen Pembimbing II"),
        Pair("Bapak Anton Prafanto, S.Kom., MT.", "Dosen Penguji I"),
        Pair("Ibu Prof. Dr. Anindita Septiarini, ST., M.Cs.", "Dosen Penguji II"),
        Pair(
            "Bapak Awang Harsa Kridalaksana, S.Kom., M.Kom.",
            "Koordinator Program Studi Informatika"
        ),
        Pair("Bapak Prof. Dr. Ir. H. Tamrin, S.T., M.T., IPU.", "Dekan Fakultas Teknik")
    )
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Terima Kasih",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            HorizontalDivider()
        }
        items(credits) { item ->
            ListItem(
                headlineContent = {
                    Text(
                        item.first
                    )
                },
                supportingContent = {
                    Text(item.second)
                }
            )
            HorizontalDivider()
        }
    }
}

private data class SourceItem(val title: String, val leadingImageUrl: String, val sourceUrl: String)

@Composable
private fun AboutPageThree(modifier: Modifier = Modifier) {
    val sources = listOf(
        SourceItem(
            "Playlist Youtube dari Daniel Harding mengenai JPEG Decoding",
            "https://i.ytimg.com/vi/CPT4FSkFUgs/hqdefault.jpg",
            "https://youtube.com/playlist?list=PLpsTn9TA_Q8VMDyOPrDKmSJYt1DLgDZU4&si=YGMEuqiCY5x_drSG"
        ),
        SourceItem(
            "JPEG DCT, Discrete Cosine Transform - Computerphile",
            "https://i.ytimg.com/vi/n_uNPbdenRs/hqdefault.jpg",
            "https://youtu.be/n_uNPbdenRs"
        ),
        SourceItem(
            "Secrets Hidden in Images (Steganography) - Computerphile",
            "https://i.ytimg.com/vi/TWEXCYQKyDc/hqdefault.jpg",
            "https://youtu.be/TWEXCYQKyDc"
        ),
        SourceItem(
            "The Unreasonable Effectiveness of JPEG: A Signal Processing Approach - Reducible",
            "https://i.ytimg.com/vi/0me3guauqOU/maxresdefault.jpg",
            "https://youtu.be/0me3guauqOU"
        ),
        SourceItem(
            "PirateSoftware Password Management",
            "https://i.ytimg.com/vi/Udf44K6rt-E/maxresdefault.jpg",
            "https://youtube.com/shorts/Udf44K6rt-E"
        ),
        SourceItem(
            "ITU Rec. T.81: Persyaratan dan pedoman untuk JPEG",
            "https://login.itu.int/public/share/itu/logo-ITU.png",
            "https://www.w3.org/Graphics/JPEG/itu-t81.pdf"
        ),
        SourceItem(
            "Spesifikasi JPEG File Interchange Format Versi 1.02 (ITU T.871)",
            "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c3/JPEG_format_logo.svg/190px-JPEG_format_logo.svg.png",
            "https://www.w3.org/Graphics/JPEG/jfif3.pdf"
        ),
        SourceItem(
            "Jetpack Compose",
            "https://raw.githubusercontent.com/github/explore/ae48d1ca3274c0c3a90f872e605eaef069a16771/topics/jetpack-compose/jetpack-compose.png",
            "https://developer.android.com/develop/ui/compose"
        ),
        SourceItem(
            "JPEG 'files' & Colour - Computerphile",
            "https://i.ytimg.com/vi/n_uNPbdenRs/maxresdefault.jpg",
            "https://youtu.be/n_uNPbdenRs"
        ),
        SourceItem(
            "Dokumentasi skimage.metrics - scikit-image",
            "https://scikit-image.org/docs/stable/_static/logo.png",
            "https://scikit-image.org/docs/stable/api/skimage.metrics.html#skimage.metrics.peak_signal_noise_ratio"
        )
    )
    val localContext = LocalContext.current
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "Sumber Daya",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            HorizontalDivider()
        }
        items(sources) { item ->
            ListItem(
                headlineContent = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingContent = {
                    AsyncImage(
                        model = item.leadingImageUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.FillHeight
                    )
                },
                trailingContent = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.sourceUrl))
                        localContext.startActivity(intent)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_open_in_browser),
                            contentDescription = "Buka di browser"
                        )
                    }
                },
                modifier = Modifier.defaultMinSize(minHeight = 72.dp)
            )
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutPageOnePreview(modifier: Modifier = Modifier) {
    StegAndroTheme {
        AboutPageOne()
    }
}