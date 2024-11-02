package io.github.gthght.stegandro.presentation.extract

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtractRoute(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit = {},
    navigateToResult: (String, String) -> Unit = { _, _ -> },
    navigateToHelp:() -> Unit = {},
    state: ExtractState = ExtractState(),
    onImageSelected: (Uri?) -> Unit = {},
    validateInput: (String) -> Boolean = { _ -> true }
) {

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    val scrollState = rememberScrollState()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    val keyFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                onImageSelected(it)
            }
        }

    var keyField by rememberSaveable {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = state.error) {
        if (state.error != null) {
            focusManager.clearFocus()
            val lowercaseError = state.error.lowercase()
            if (lowercaseError.contains("kunci")){
                keyFocusRequester.requestFocus()
            }
            snackBarHostState.showSnackbar(state.error)
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
                    Text(text = "Pembacaan Pesan Pada Gambar", style = MaterialTheme.typography.titleMedium)
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
                                Text("Panduan Penggunaan Pembacaan Pesan Pada Gambar")
                            }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(onClick = navigateToHelp) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_help),
                                contentDescription = "Panduan Pembacaan Pesan",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState, modifier = Modifier.imePadding()) }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).imePadding().verticalScroll(scrollState)) {
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
                                text = "Silakan pilih gambar dari galeri Anda yang ingin diekstraksi pesan rahasianya.",
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
                Button(
                    onClick = {
                        singlePhotoPickerLauncher.launch(
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
            Spacer(modifier = Modifier.size(20.dp))
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
            Spacer(modifier = Modifier.size(16.dp))
            Button(
                onClick = {
                    if (validateInput(keyField)) {
                        val uri = Uri.encode(state.imagePathName.toString()).replace('%', '|')
                        val key = keyField.replace('/', '|')
                        navigateToResult(uri, key)
                    }
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_end),
                    contentDescription = "Ikon Embedding",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Mulai Extraction")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ExtractRoutePreview() {
    StegAndroTheme {
        ExtractRoute()
    }
}