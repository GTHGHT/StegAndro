package io.github.gthght.stegandro.presentation.qualitytest.info

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityTestInfoRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {}
) {
    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(text = "Nilai Kualitas Gambar", style = MaterialTheme.typography.titleMedium)
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
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            Text(
                text = "Kualitas gambar pada aplikasi ini diuji menggunakan metode " +
                        "Mean Square Error (MSE) dan Peak Signal-to-Noise Ratio (PSNR). " +
                        "Fungsionalitas ini hanya ditujukan untuk gambar steganografi yang " +
                        "dihasilkan oleh aplikasi ini. ",
                textAlign = TextAlign.Justify
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Berikut adalah rentang nilai PSNR dan kualitas gambarnya:", textAlign = TextAlign.Justify)
            Spacer(modifier = Modifier.size(16.dp))
            Row (Modifier.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)){
                TableCell(text = "Nilai PSNR", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                TableCell(text = "Kualitas Gambar", textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                TableCell(text = ">= 60 dB")
                TableCell(text = "Sempurna")
            }
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                TableCell(text = "50 s/d 59,99 dB")
                TableCell(text = "Sangat Baik")
            }
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                TableCell(text = "40 s/d 49,99 dB")
                TableCell(text = "Baik")
            }
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                TableCell(text = "30 s/d 39,99 dB")
                TableCell(text = "Cukup Baik")
            }
            Row(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                TableCell(text = "< 30 dB")
                TableCell(text = "Buruk")
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = "Nilai Mean Squared Error (MSE) yang semakin rendah mengindikasikan " +
                    "semakin sedikitnya distorsi pada gambar steganografi relatif terhadap gambar " +
                    "aslinya. Sebaliknya, nilai Peak Signal-to-Noise Ratio (PSNR) yang semakin " +
                    "tinggi menunjukkan kualitas gambar steganografi yang semakin baik.",
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
    weight: Float = 1f
) {
    Text(
        text = text,
        textAlign = textAlign,
        fontWeight = fontWeight,
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .weight(weight)
            .padding(8.dp)
    )
}

@Preview(showBackground = false)
@Composable
fun QualityTestInfoPreview() {
    StegAndroTheme {
        QualityTestInfoRoute()
    }
}