package io.github.gthght.stegandro

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.github.gthght.stegandro.presentation.about.AboutRoute
import io.github.gthght.stegandro.presentation.embed.EmbedRoute
import io.github.gthght.stegandro.presentation.embed.EmbedViewModel
import io.github.gthght.stegandro.presentation.embed.help.EmbedHelpRoute
import io.github.gthght.stegandro.presentation.embed.result.EmbedResultRoute
import io.github.gthght.stegandro.presentation.embed.result.EmbedResultViewModel
import io.github.gthght.stegandro.presentation.extract.ExtractRoute
import io.github.gthght.stegandro.presentation.extract.ExtractViewModel
import io.github.gthght.stegandro.presentation.extract.help.ExtractHelpRoute
import io.github.gthght.stegandro.presentation.extract.result.ExtractResultRoute
import io.github.gthght.stegandro.presentation.extract.result.ExtractResultViewModel
import io.github.gthght.stegandro.presentation.home.HomeRoute
import io.github.gthght.stegandro.presentation.introduction.IntroductionRoute
import io.github.gthght.stegandro.presentation.navigation.StegAndroDestination
import io.github.gthght.stegandro.presentation.qualitytest.QualityTestRoute
import io.github.gthght.stegandro.presentation.qualitytest.QualityTestViewModel
import io.github.gthght.stegandro.presentation.qualitytest.help.QualityTestHelpRoute
import io.github.gthght.stegandro.presentation.qualitytest.info.QualityTestInfoRoute
import io.github.gthght.stegandro.presentation.theme.StegAndroTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StegAndroTheme {
                StegAndroApp()
            }
        }
    }
}


@Composable
fun StegAndroApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = StegAndroDestination.Intro.route
) {
    val composableScope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        val navigateUpFunction: () -> Unit = {
            navController.navigateUp()
        }
        composable(
            route = StegAndroDestination.Home.route
        ) {
            HomeRoute(
                navigateToEmbed = {
                    navController.navigate(StegAndroDestination.Embed.route)
                },
                navigateToExtract = {
                    navController.navigate(StegAndroDestination.Extract.route)
                },
                navigateToTest = {
                    navController.navigate(StegAndroDestination.QualityTest.route)
                },
                navigateToAbout = {
                    navController.navigate(StegAndroDestination.About.route)
                }
            )
        }
        composable(
            route = StegAndroDestination.Intro.route
        ) {
            IntroductionRoute(onDone = {
                navController.navigate(StegAndroDestination.Home.route) {
                    popUpTo(StegAndroDestination.Intro.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(
            route = StegAndroDestination.Embed.route
        ) {
            val embedViewModel: EmbedViewModel = hiltViewModel<EmbedViewModel>()
            EmbedRoute(
                state = embedViewModel.state,
                navigateBack = navigateUpFunction,
                navigateToResult = { uri, key, message, isSampled, size ->
                    navController.navigate(
                        StegAndroDestination.EmbedResult.createRoute(
                            uri,
                            key,
                            message,
                            isSampled,
                            size
                        )
                    ) {
                        popUpTo(StegAndroDestination.Home.route)
                    }
                },
                navigateToHelp = {
                    navController.navigate(StegAndroDestination.EmbedHelp.route)
                },
                onImageSelected = { uri, isCamera ->
                    uri?.let {
                        composableScope.launch {
                            embedViewModel.setImage(it, isCamera)
                        }
                    }
                },
                validateInput = { key, message ->
                    embedViewModel.validateInput(key, message)
                }
            )
        }
        composable(
            route = StegAndroDestination.EmbedResult.route,
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("key") { type = NavType.StringType },
                navArgument("message") { type = NavType.StringType },
                navArgument("isSampled") { type = NavType.BoolType },
                navArgument("size") { type = NavType.IntType }
            )
        ) {
            val uriArgument = it.arguments?.getString("uri") ?: ""
            val uri = Uri.decode(uriArgument.replace(0.toChar(), '%')).toUri()
            val keyArgument = it.arguments?.getString("key") ?: ""
            val key = keyArgument.replace(0.toChar(), '/')
            val messageArgument = it.arguments?.getString("message") ?: ""
            val message = messageArgument.replace(0.toChar(), '/')
            val isSampled = it.arguments?.getBoolean("isSampled") ?: false
            val size = it.arguments?.getInt("size") ?: -1
            val embedResultViewModel: EmbedResultViewModel = hiltViewModel<EmbedResultViewModel>()
            embedResultViewModel.setOriginalImageSize(size)
            EmbedResultRoute(
                state = embedResultViewModel.state,
                navigateBack = navigateUpFunction,
                navigateToEmbed = {
                    navController.navigate(StegAndroDestination.Embed.route) {
                        popUpTo(StegAndroDestination.Home.route)
                    }
                },
                loadEmbedImage = {
                    composableScope.launch {
                        embedResultViewModel.embedImage(uri, key, message, isSampled)
                    }
                },
                loadImageMetaData = { ctx ->
                    embedResultViewModel.loadImageMetaData(ctx)
                },
                saveImageTo = {
                    composableScope.launch {
                        embedResultViewModel.copyImageToDestination(it)
                    }
                }
            )
        }
        composable(
            route = StegAndroDestination.EmbedHelp.route
        ) {
            EmbedHelpRoute(navigateBack = navigateUpFunction,)
        }
        composable(
            route = StegAndroDestination.Extract.route
        ) {
            val extractViewModel: ExtractViewModel = hiltViewModel()
            ExtractRoute(
                navigateBack = navigateUpFunction,
                navigateToResult = { uri, key ->
                    navController.navigate(
                        StegAndroDestination.ExtractResult.createRoute(
                            uri = uri,
                            key = key
                        )
                    ) {
                        popUpTo(StegAndroDestination.Home.route)
                    }
                },
                navigateToHelp = {
                    navController.navigate(StegAndroDestination.ExtractHelp.route)
                },
                state = extractViewModel.state,
                onImageSelected = { uri ->
                    uri?.let {
                        extractViewModel.setImage(it)
                    }
                },
                validateInput = { key ->
                    extractViewModel.validateInput(key)
                }
            )
        }
        composable(
            route = StegAndroDestination.ExtractResult.route,
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("key") { type = NavType.StringType }
            )
        ) {
            val uriArgument = it.arguments?.getString("uri") ?: ""
            val uri = Uri.decode(uriArgument.replace('|', '%')).toUri()
            val keyArgument = it.arguments?.getString("key") ?: ""
            val key = keyArgument.replace('|', '/')
            val extractViewModel: ExtractResultViewModel = hiltViewModel<ExtractResultViewModel>()
            ExtractResultRoute(
                navigateBack = navigateUpFunction,
                state = extractViewModel.state,
                navigateToExtract = {
                    navController.navigate(StegAndroDestination.Extract.route) {
                        popUpTo(StegAndroDestination.Home.route)
                    }
                },
                loadExtractImage = {
                    composableScope.launch {
                        extractViewModel.extractImage(uri, key)
                    }
                }

            )
        }
        composable(
            route = StegAndroDestination.ExtractHelp.route
        ) {
            ExtractHelpRoute(navigateBack = navigateUpFunction,)
        }
        composable(
            route = StegAndroDestination.QualityTest.route
        ) {
            val qualityTestViewModel: QualityTestViewModel = hiltViewModel<QualityTestViewModel>()
            QualityTestRoute(
                navigateBack = navigateUpFunction,
                navigateToInfo = {
                    navController.navigate(StegAndroDestination.QualityTestInfo.route)
                },
                navigateToHelp = {
                    navController.navigate(StegAndroDestination.QualityTestHelp.route)
                },
                state = qualityTestViewModel.state,
                onOriginalImageSelected = { uri ->
                    uri?.let {
                        qualityTestViewModel.setOriginalImage(it)
                    }
                },
                onModifiedImageSelected = { uri ->
                    uri?.let {
                        qualityTestViewModel.setModifiedImage(it)
                    }
                },
                compareImages = {
                    composableScope.launch {
                        qualityTestViewModel.runStatistic()
                    }
                }
            )
        }
        composable(
            route = StegAndroDestination.QualityTestInfo.route
        ) {
            QualityTestInfoRoute(navigateBack = navigateUpFunction,)
        }
        composable(
            route = StegAndroDestination.QualityTestHelp.route
        ) {
            QualityTestHelpRoute(navigateBack = navigateUpFunction,)
        }
        composable(
            route = StegAndroDestination.About.route
        ) {
            AboutRoute(
                navigateBack = navigateUpFunction
            )
        }
    }
}