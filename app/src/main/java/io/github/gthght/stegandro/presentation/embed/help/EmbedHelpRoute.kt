package io.github.gthght.stegandro.presentation.embed.help

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
fun EmbedHelpRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {}
) {
    val extractHelpContent = listOf(
        GuideContent(
            "https://i.imgur.com/CD5Waa9.jpeg",
            "Penyisipan pesan pada gambar membutuhkan tiga masukan, yaitu " +
                    "gambar, kunci, dan pesan. Untuk memilih gambar, tekanlah tombol 'Galeri' untuk " +
                    "memilih gambar dari galeri atau tekanlah tombol 'Kamera' untuk mengambil " +
                    "gambar dari kamera secara langsung. Setelah gambar dipilih, maka isilah " +
                    "masukan kunci dan pesan."
        ),
        GuideContent(
            "https://i.imgur.com/OUGR8pO.png",
            "Jika gambar telah terpilih serta kunci dan pesan telah terisi, maka " +
                    "tekanlah tombol 'Mulai Embedding'. Anda akan diarahkan ke halaman " +
                    "hasil penyisipan pesan pada gambar."
        ),
        GuideContent(
            "https://i.imgur.com/xRjfvfh.png",
            "Silahkan tunggu beberapa detik sampai proses penyisipan pada gambar selesai."
        ),
        GuideContent(
            "https://i.imgur.com/yyBNvD7.png",
            "Jika proses sudah selesai, maka gambar hasil steganografi akan ditampilkan" +
                    " beserta informasi tambahan mengenai gambar tersebut. Anda dapat membagikan " +
                    "gambar atau menyimpan gambar pada lokasi lain menggunakan tombol yang tertera."
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
                    text = "Panduan Penyisipan Pesan Pada Gambar",
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