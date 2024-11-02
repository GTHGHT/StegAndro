package io.github.gthght.stegandro.presentation.navigation

sealed class StegAndroDestination(val route: String) {
    data object Intro: StegAndroDestination("intro")
    data object Home: StegAndroDestination("home")
    data object Embed: StegAndroDestination("embed")
    data object EmbedResult: StegAndroDestination("embed/result/{uri}/{key}/{message}/{isSampled}/{size}"){
        fun createRoute(uri: String, key: String, message: String, isSampled: Boolean, size:Int) = "embed/result/$uri/$key/$message/$isSampled/$size"
    }
    data object EmbedHelp: StegAndroDestination("embed/help")
    data object Extract: StegAndroDestination("extract")
    data object ExtractResult: StegAndroDestination("extract/result/{uri}/{key}"){
        fun createRoute(uri: String, key: String) = "extract/result/$uri/$key"
    }
    data object ExtractHelp: StegAndroDestination("extract/help")
    data object About: StegAndroDestination("about")
    data object QualityTest: StegAndroDestination("quality_test")
    data object QualityTestInfo: StegAndroDestination("quality_test/info")
    data object QualityTestHelp: StegAndroDestination("quality_test/help")
}