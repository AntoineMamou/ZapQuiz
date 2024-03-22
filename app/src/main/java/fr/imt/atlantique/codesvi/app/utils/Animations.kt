package fr.imt.atlantique.codesvi.app.utils

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut

fun scaleInEnterTransition() = scaleIn(
    initialScale = .9f,
    animationSpec = tween(300)
) + fadeIn(
    animationSpec = tween(300)
)

fun scaleOutExitTransition() = scaleOut(
    targetScale = 1.1f,
    animationSpec = tween(300)
) + fadeOut(
    animationSpec = tween(300)
)

fun scaleInPopEnterTransition() = scaleIn(
    initialScale = 1.1f,
    animationSpec = tween(300)
) + fadeIn(
    animationSpec = tween(300)
)

fun scaleOutPopExitTransition() = scaleOut(
    targetScale = .9f,
    animationSpec = tween(300)
) + fadeOut(
    animationSpec = tween(300)
)