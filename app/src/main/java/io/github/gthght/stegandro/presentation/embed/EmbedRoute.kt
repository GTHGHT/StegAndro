package io.github.gthght.stegandro.presentation.embed

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.github.gthght.stegandro.R
import io.github.gthght.stegandro.presentation.component.BeveledIcon
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme
import io.github.gthght.stegandro.util.getImageSizeFromURI
import io.github.gthght.stegandro.util.getImageUri
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmbedRoute(
    modifier: Modifier = Modifier,
    state: EmbedState = EmbedState(),
    navigateBack: () -> Unit = {},
    navigateToResult: (String, String, String, Boolean, Int) -> Unit = { _, _, _, _, _ -> },
    navigateToHelp: () -> Unit = {},
    onImageSelected: (Uri?, Boolean) -> Unit = { _, _ -> },
    validateInput: (String, String) -> Boolean = { _, _ -> true },
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val messageFocusRequester = remember { FocusRequester() }
    val keyFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val localContext = LocalContext.current

    val scrollState = rememberScrollState()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                onImageSelected(it, false)
            }
        }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                onImageSelected(imageUri, true)
            }
        }

    var keyField by rememberSaveable {
        mutableStateOf("")
    }

    var messageField by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = state.error) {
        if (state.error != null) {
            focusManager.clearFocus()
            val lowercaseError = state.error.lowercase()
            if (lowercaseError.contains("kunci")){
                keyFocusRequester.requestFocus()
            } else if (lowercaseError.contains("pesan")){
                messageFocusRequester.requestFocus()
            }
            snackBarHostState.showSnackbar(message = state.error)
        }
    }

    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope {
            scrollState.scrollBy(keyboardHeight.toFloat())
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
                    Text(text = "Penyisipan Pesan Pada Gambar", style = MaterialTheme.typography.titleMedium)
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
                                Text("Panduan Penggunaan Penyisipan Pesan Pada Gambar")
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = navigateToHelp) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_help),
                                contentDescription = "Panduan Penyisipan Pesan",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState, modifier = Modifier.imePadding()) }) { innerPadding ->
        Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(innerPadding)
                    .imePadding()
            ) {
                Box {
                    Card(
                        Modifier
                            .aspectRatio(ratio = 16f / 10f)
                            .padding(16.dp)
                            .padding(bottom = 8.dp)
                    ) {
                        if (state.imagePathName == null) {
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
                                    text = "Pilih Gambar",
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Silahkan pilih gambar dari galeri atau kamera Anda yang akan disisipkan pesan",
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(0.75f)
                                )
                            }
                        } else {
                            AsyncImage(
                                model = state.imagePathName,
                                contentDescription = "SelectedImage",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                    Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                        //Pilih Gambar Dari Gallery
                        Button(onClick = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_gallery),
                                contentDescription = "Buka Galeri",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = modifier.size(8.dp))
                            Text(text = "Galeri")
                        }
                        Spacer(modifier = Modifier.fillMaxWidth(0.1f))

                        //Pilih Gambar Dari Kamera
                        Button(onClick = {
                            val uri = getImageUri(localContext, "Camera")
                            imageUri = uri
                            cameraLauncher.launch(uri)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_camera),
                                contentDescription = "Buka Kamera",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = modifier.size(8.dp))
                            Text(text = "Kamera")
                        }
                    }
                }
                Row(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Kapasitas Gambar:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${state.imageCapacity ?: '0'} Karakter",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                TextField(
                    label = {
                        Text(text = "Kunci")
                    },
                    value = keyField,
                    onValueChange = { s ->
                        keyField = s
                    },
                    modifier = Modifier
                        .focusRequester(keyFocusRequester)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
                TextField(
                    label = {
                        Text(text = "Pesan")
                    },
                    value = messageField,
                    onValueChange = { s ->
                        messageField = s
                    },
                    supportingText = {
                        Text(
                            text = "${messageField.count()} / ${state.imageCapacity ?: '0'}",
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    minLines = 2,
                    modifier = Modifier
                        .focusRequester(messageFocusRequester)
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (validateInput(keyField, messageField)) {
                            val uri = Uri.encode(state.imagePathName.toString()).replace('%', 0.toChar())
                            val key = keyField.replace('/', 0.toChar())
                            val message = messageField.replace('/', 0.toChar())
                            val size = if (state.fromCamera) -1 else getImageSizeFromURI(localContext, state.imagePathName)
                            navigateToResult(uri, key, message, state.fromCamera, size)
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_start),
                        contentDescription = "Ikon Embedding",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Mulai Embedding")
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
private fun EmbedRoutePreview() {
    StegAndroTheme {
        EmbedRoute()
    }
}