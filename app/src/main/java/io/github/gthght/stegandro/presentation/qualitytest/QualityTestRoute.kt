package io.github.gthght.stegandro.presentation.qualitytest

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.gthght.stegandro.R
import io.github.gthght.stegandro.presentation.component.BeveledIcon
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityTestRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    navigateToInfo: () -> Unit = {},
    navigateToHelp: () -> Unit = {},
    state: QualityTestState,
    onOriginalImageSelected: (Uri?) -> Unit = {},
    onModifiedImageSelected: (Uri?) -> Unit = {},
    compareImages: () -> Unit = {}
) {

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val originalPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                onOriginalImageSelected(it)
            }
        }
    val modifiedPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                onModifiedImageSelected(it)
            }
        }

    LaunchedEffect(key1 = state.modifiedImage, key2 = state.originalImage) {
        if (state.originalImage != null && state.modifiedImage != null) {
            compareImages()
        }
    }

    LaunchedEffect(key1 = state.error) {
        if (state.error != null) {
            snackBarHostState.showSnackbar(state.error)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "Pengujian Kualitas Gambar",
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
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text("Info Nilai Kualitas Gambar")
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = navigateToInfo) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Info Tentang Kualitas Gambar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text("Panduan Penggunaan Kualitas Gambar")
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = navigateToHelp) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_help),
                                contentDescription = "Info Tentang Kualitas Gambar",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        Box {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    Card(
                        Modifier
                            .aspectRatio(ratio = 16f / 10f)
                            .padding(16.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        if (state.originalImage == null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize()
                            ) {
                                BeveledIcon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_image),
                                    contentDescription = "Ikon Image"
                                )
                                Spacer(modifier = Modifier.size(size = 16.dp))
                                Text(
                                    text = "Pilih Gambar Asli",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Silakan pilih gambar asli sebelum dilakukannya steganografi dari galeri Anda.",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(0.75f)
                                )
                            }
                        } else {
                            AsyncImage(
                                model = state.originalImage,
                                contentDescription = "SelectedImage",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                    Button(
                        onClick = {
                            originalPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.BottomCenter)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = "Buka Galeri",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = modifier.size(8.dp))
                        Text(text = "Galeri")
                    }
                }

                Box {
                    Card(
                        Modifier
                            .aspectRatio(ratio = 16f / 10f)
                            .padding(16.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        if (state.modifiedImage == null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxSize()
                            ) {
                                BeveledIcon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_image),
                                    contentDescription = "Ikon Image"
                                )
                                Spacer(modifier = Modifier.size(size = 16.dp))
                                Text(
                                    text = "Pilih Gambar Steganografi",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Silakan pilih gambar yang telah dilakukan steganografi dari galeri Anda.",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(0.75f)
                                )
                            }
                        } else {
                            AsyncImage(
                                model = state.modifiedImage,
                                contentDescription = "SelectedImage",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                    Button(
                        onClick = {
                            modifiedPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        modifier
                            .fillMaxWidth(0.7f)
                            .align(Alignment.BottomCenter)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = "Buka Galeri",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = modifier.size(8.dp))
                        Text(text = "Galeri")
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("MSE Merah:", style = MaterialTheme.typography.bodyLarge)
                    if (state.metricsResult.mseChannelOne != null) {
                        Text(
                            "%.10f dB".format(state.metricsResult.mseChannelOne),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("MSE Hijau:", style = MaterialTheme.typography.bodyLarge)
                    if (state.metricsResult.mseChannelTwo != null) {
                        Text(
                            "%.10f dB".format(state.metricsResult.mseChannelTwo),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("MSE Biru:", style = MaterialTheme.typography.bodyLarge)
                    if (state.metricsResult.mseChannelThree != null) {
                        Text(
                            "%.10f dB".format(state.metricsResult.mseChannelThree),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("MSE Total:", style = MaterialTheme.typography.bodyLarge)
                    if (state.metricsResult.mseCombined != null) {
                        Text(
                            "%.10f dB".format(state.metricsResult.mseCombined),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("PSNR:", style = MaterialTheme.typography.bodyLarge)
                    state.metricsResult.psnr.let {
                        if (it != null) {
                            Text(
                                if (it == Double.POSITIVE_INFINITY)
                                    "Tidak Terdefinisi"
                                else
                                    "%.10f dB".format(it),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Kualitas Gambar:", style = MaterialTheme.typography.bodyLarge)
                    state.metricsResult.psnr.let {
                        if (it != null) {
                            Text(
                                when {
                                    it == Double.POSITIVE_INFINITY -> "Gambar Identik"
                                    it >= 60.0 -> "Sempurna"
                                    it >= 50.0 -> "Sangat Baik"
                                    it >= 40.0 -> "Baik"
                                    it >= 30.0 -> "Cukup Baik"
                                    it < 30.0 -> "Buruk"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            if (state.isLoading) {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.surface.copy(0.8f))
                        .clickable(enabled = false, onClick = {}),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        strokeCap = StrokeCap.Round,
                        trackColor = MaterialTheme.colorScheme.secondaryContainer,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tunggu Beberapa Detik...")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QualityTestRoutePreview() {
    StegAndroTheme {
        QualityTestRoute(state = QualityTestState())
    }
}