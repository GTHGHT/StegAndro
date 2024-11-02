package io.github.gthght.stegandro.presentation.extract.help

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
fun ExtractHelpRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {}
) {
    val extractHelpContent = listOf(
        GuideContent(
            "https://i.imgur.com/ks005wv.jpeg",
            "Pembacaan pesan pada gambar membutuhkan dua masukan, yaitu gambar dan " +
                    "kunci. Untuk memilih gambar, tekanlah tombol 'Galeri' untuk memilih gambar " +
                    "dari galeri. Setelah gambar dipilih, maka isilah masukan kunci."
        ),
        GuideContent(
            "https://i.imgur.com/CoIKMYu.png",
            "Jika gambar telah terpilih dan kunci telah terisi, maka tekanlah tombol " +
                    "'Mulai Extraction'. Anda akan diarahkan ke halaman hasil pembacaan " +
                    "pesan pada gambar."
        ),
        GuideContent(
            "https://i.imgur.com/i4abcQT.png",
            "Silahkan tunggu beberapa detik sampai proses pembacaan pada gambar selesai."
        ),
        GuideContent(
            "https://i.imgur.com/JAmitqx.png",
            "Jika proses sudah selesai, maka pesan rahasia akan ditampilkan. " +
                    "Anda dapat menyalin pesan rahasia menggunakan tombol yang tertera."
        ),
    )

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(
                    text = "Panduan Pembacaan Pesan Pada Gambar",
                    style = MaterialTheme.typography.titleMedium
                )
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
            contents = extractHelpContent,
            modifier = modifier.padding(innerPadding)
        )
    }
}