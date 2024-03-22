package fr.imt.atlantique.codesvi.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import fr.imt.atlantique.codesvi.app.ui.screens.settings.Theme

private val DarkColorScheme = darkColorScheme(
    onTertiary = YellowDefaultDarkerOn,
    onSecondary = YellowDefaultDarkerOn,
    onBackground = LightDark,
    surface = Color.White,
    onSurface = LightGrayText,
    secondaryContainer = Color.White,
    onSecondaryContainer = LightGrayText,


    primary = yellow_1,
    onPrimary = blue_3 ,
    secondary = blue_1,
    tertiary = yellow_2,
    background = blue_2
)

private val LightColorScheme = lightColorScheme(


    onTertiary = YellowDefaultDarkerOn,
    onSecondary = YellowDefaultDarkerOn,
    onBackground = LightDark,
    surface = Color.White,
    onSurface = LightGrayText,
    secondaryContainer = Color.White,
    onSecondaryContainer = LightGrayText,


    primary = yellow_1,
    onPrimary = blue_3 ,
    secondary = blue_1,
    tertiary = yellow_2,
    background = blue_2
)


val LightAndroidGradientColors = GradientColors(container = DarkGreenGray95)

val DarkAndroidGradientColors = GradientColors(container = Color.Black)

val LightAndroidBackgroundTheme = BackgroundTheme(color = DarkGreenGray95)

val DarkAndroidBackgroundTheme = BackgroundTheme(color = Color.Black)

@Composable
fun AppTheme(
    theme: Theme,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        theme == Theme.MATERIAL_YOU && supportsDynamicTheming() -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        theme == Theme.FOLLOW_SYSTEM -> {
            if (isDarkTheme) DarkColorScheme else LightColorScheme
        }

        theme == Theme.DARK_THEME -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDarkTheme
        }
    }

    // Gradient colors
    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )
    val gradientColors = when {
        theme == Theme.DARK_THEME -> DarkAndroidGradientColors
        theme == Theme.LIGHT_THEME -> LightAndroidGradientColors
        theme == Theme.FOLLOW_SYSTEM -> if (isDarkTheme) DarkAndroidGradientColors else LightAndroidGradientColors
        theme == Theme.MATERIAL_YOU && supportsDynamicTheming() -> emptyGradientColors
        else -> defaultGradientColors
    }

    // Background theme
    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )
    val backgroundTheme = when {
        theme == Theme.DARK_THEME -> DarkAndroidBackgroundTheme
        theme == Theme.LIGHT_THEME -> LightAndroidBackgroundTheme
        theme == Theme.FOLLOW_SYSTEM -> if (isDarkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        theme == Theme.MATERIAL_YOU && supportsDynamicTheming() -> defaultBackgroundTheme
        else -> defaultBackgroundTheme
    }
    val tintTheme = when {
        theme == Theme.DARK_THEME || theme == Theme.LIGHT_THEME -> TintTheme()
        theme == Theme.FOLLOW_SYSTEM -> if (isDarkTheme) TintTheme() else TintTheme(colorScheme.primary)
        theme == Theme.MATERIAL_YOU && supportsDynamicTheming() -> TintTheme()
        else -> TintTheme()
    }

    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S