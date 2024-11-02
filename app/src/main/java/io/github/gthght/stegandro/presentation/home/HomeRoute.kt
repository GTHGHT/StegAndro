package io.github.gthght.stegandro.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.github.gthght.stegandro.R
import io.github.gthght.stegandro.presentation.component.CardIconButton

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    navigateToEmbed: () -> Unit = {},
    navigateToExtract: () -> Unit = {},
    navigateToTest: () -> Unit = {},
    navigateToAbout: () -> Unit = {}
) {
    val currLayoutDirection = LocalLayoutDirection.current
    Scaffold { innerPadding ->
        Column(
            modifier = modifier.padding(
                start = innerPadding.calculateStartPadding(
                    currLayoutDirection
                ),
                end = innerPadding.calculateEndPadding(
                    currLayoutDirection
                ),
                bottom = innerPadding.calculateBottomPadding(),
            )
        ) {
            Spacer(
                modifier = Modifier
                    .height(height = innerPadding.calculateTopPadding())
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
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
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 172.dp)) {
                item {
                    CardIconButton(
                        title = "Embed Gambar",
                        description = "Penyisipan pesan tersembunyi pada gambar",
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_start),
                        contentDescription = "Ikon Embedding",
                        buttonText = "Mulai",
                        onClick = navigateToEmbed
                    )
                }
                item {
                    CardIconButton(
                        title = "Extract Gambar",
                        description = "Pembacaan pesan tersembunyi pada gambar",
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_end),
                        contentDescription = "Ikon Extraction",
                        buttonText = "Mulai",
                        onClick = navigateToExtract
                    )
                }
                item {
                    CardIconButton(
                        title = "Kualitas Gambar",
                        description = "Pengujian kualitas gambar menggunakan metode MSE & PSNR",
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_compare),
                        contentDescription = "Ikon Pengujian",
                        buttonText = "Mulai",
                        onClick = navigateToTest
                    )
                }
                item {
                    CardIconButton(
                        title = "Tentang",
                        description = "Data diri pengembang, apresiasi, dan sumber yang digunakan",
                        imageVector = Icons.TwoTone.Person,
                        contentDescription = "Ikon Tentang",
                        buttonText = "Mulai",
                        onClick = navigateToAbout
                    )
                }
            }


        }
    }
}
