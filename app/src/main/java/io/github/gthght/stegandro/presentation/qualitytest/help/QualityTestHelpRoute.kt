package io.github.gthght.stegandro.presentation.qualitytest.help

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.gthght.stegandro.presentation.common.GuideContent
import io.github.gthght.stegandro.presentation.common.GuideScreenSlider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityTestHelpRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {}
) {
    val qtHelpContent = listOf(
        GuideContent(
            "https://i.imgur.com/pHA8WOO.jpeg",
            "Untuk memulai pengujian kualitas gambar pilihlah gambar asli dan gambar " +
                    "hasil steganografi menggunakan tombol yang ditunjukkan."
        ),
        GuideContent(
            "https://i.imgur.com/43RLinh.png",
            "Jika gambar asli dan gambar hasil steganografi telah terpilih, maka sistem " +
                    "akan otomatis menguji kualitas gambar dari dua gambar tersebut."
        )
    )

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(text = "Panduan Kualitas Gambar", style = MaterialTheme.typography.titleMedium)
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
        GuideScreenSlider(
            contents = qtHelpContent,
            modifier = modifier.padding(innerPadding)
        )
    }
}