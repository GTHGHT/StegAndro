package io.github.gthght.stegandro.presentation.embed.result

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import io.github.gthght.stegandro.R
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmbedResultRoute(
    modifier: Modifier = Modifier,
    state: EmbedResultState = EmbedResultState(),
    navigateBack: () -> Unit = {},
    navigateToEmbed: () -> Unit = {},
    loadEmbedImage: () -> Unit = {},
    loadImageMetaData: (Context) -> Unit = {},
    saveImageTo: (Uri) -> Unit = {},
) {

    val createImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.CreateDocument("image/jpeg")) { uri ->
            uri?.let {
                saveImageTo(it)
            }
        }

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val localContext = LocalContext.current

    LaunchedEffect(state.resultPath) {
        if (state.resultPath == null) {
            loadEmbedImage()
        } else if (state.resultMetaData?.resolution == null) {
            Log.d("EmbedResultRoute", "First Load Image Data")
            delay(3000)
            loadImageMetaData(localContext)
        }
    }

    LaunchedEffect(state.snackBarMessage) {
        if (state.snackBarMessage != null) {
            snackBarHostState.showSnackbar(state.snackBarMessage)
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Hasil Embedding", style = MaterialTheme.typography.titleMedium)
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali Ke Beranda",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                })
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) { innerPadding ->
        if (state.isLoading) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tunggu Beberapa Detik...")
            }
        } else if (state.error != null) {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Embedding Gagal",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = state.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = navigateToEmbed, modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Ikon Kembali",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Kembali Ke Extract")
                }
            }
        } else {
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = state.resultPath,
                    contentDescription = "Hasil Steganografi",
                    modifier = Modifier
                        .padding(16.dp)
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(size = 8.dp))
                        .background(color = MaterialTheme.colorScheme.surfaceContainer)
                )
                Text(
                    text = "Embedding Berhasil",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Pesan rahasia berhasil disembunyikan ke dalam gambar. " +
                            "Bagikan gambar & berikan kunci untuk membacanya.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    "Lokasi File:",
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )

                Text(
                    state.resultMetaData?.let {
                        it.relativePath + it.fileName
                    }?: "...Loading",
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodySmall,
                )


                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Resolusi:")
                    Text(
                        state.resultMetaData?.resolution ?: "...Loading"
                    )

                }
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Text("Ukuran File:")
                    state.resultMetaData?.fileSize?.let {
                        val addition =
                            if (state.originalSize == -1) "" else "%.2f KB → ".format(state.originalSize / 1024.0)
                        Text(
                            addition + "%.2f KB".format(it / 1024.0)
                        )
                    }
                }
                Spacer(modifier = Modifier.size(24.dp))
                Button(
                    onClick = {
                        state.resultPath?.let {
                            shareImageUri(localContext, it)
                        }
                    }, modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = "Ikon Bagikan",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Bagikan Gambar")
                }
                Spacer(modifier = Modifier.size(8.dp))
                Button(
                    onClick = {
                        val filenameFormat = "yyyyMMdd_HHmmss"
                        val timeStamp: String = SimpleDateFormat(filenameFormat, Locale.US).format(
                            Date()
                        )
                        createImageLauncher.launch("$timeStamp.jpg")
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sd_card),
                        contentDescription = "Ikon Simpan",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Simpan Gambar")
                }
                Spacer(modifier = Modifier.size(8.dp))
                Button(
                    onClick = navigateToEmbed, modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Ikon Kembali",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Kembali Ke Embed")
                }
            }

        }
    }
}

private fun shareImageUri(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setType("image/jpeg")
    // This does set telegram to send in the file format but not enough to fool whatsapp
//    intent.setType("text/plain")
    startActivity(context, intent, null)
}

@Preview(showBackground = true)
@Composable
private fun EmbedResultRoutePreview() {
    StegAndroTheme {
        EmbedResultRoute(
        )
    }
}
