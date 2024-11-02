package io.github.gthght.stegandro.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import io.github.gthght.stegandro.R

// Set of Material typography styles to start with
val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val encodeSans = GoogleFont("Encode Sans")
val dmSans = GoogleFont("DM Sans")

val encodeFamily = FontFamily(
    Font(googleFont = encodeSans, fontProvider = fontProvider),
    Font(googleFont = encodeSans, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = encodeSans, fontProvider = fontProvider, weight = FontWeight.Bold)
)

val dmFamily = FontFamily(
    Font(googleFont = dmSans, fontProvider = fontProvider),
    Font(googleFont = dmSans, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = dmSans, fontProvider = fontProvider, weight = FontWeight.Bold),
)

private val typo = Typography()
val appTypography = Typography(
    displayLarge = typo.displayLarge.copy(fontFamily = encodeFamily),
    displayMedium = typo.displayMedium.copy(fontFamily = encodeFamily),
    displaySmall = typo.displaySmall.copy(fontFamily = encodeFamily),

    headlineLarge = typo.headlineLarge.copy(fontFamily = encodeFamily),
    headlineMedium = typo.headlineMedium.copy(fontFamily = encodeFamily),
    headlineSmall = typo.headlineSmall.copy(fontFamily = encodeFamily),

    titleLarge = typo.titleLarge.copy(fontFamily = encodeFamily),
    titleMedium = typo.titleMedium.copy(fontFamily = encodeFamily),
    titleSmall = typo.titleSmall.copy(fontFamily = encodeFamily),

    bodyLarge = typo.bodyLarge.copy(fontFamily = dmFamily),
    bodyMedium = typo.bodyMedium.copy(fontFamily = dmFamily),
    bodySmall = typo.bodySmall.copy(fontFamily = dmFamily),

    labelLarge = typo.labelLarge.copy(fontFamily = dmFamily),
    labelMedium = typo.labelMedium.copy(fontFamily = dmFamily),
    labelSmall = typo.labelSmall.copy(fontFamily = dmFamily),
)