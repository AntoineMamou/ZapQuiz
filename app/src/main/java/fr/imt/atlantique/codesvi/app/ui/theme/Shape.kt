package fr.imt.atlantique.codesvi.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(Dimens.ExtraSmallPadding.size),
    small = RoundedCornerShape(Dimens.SmallPadding.size),
    medium = RoundedCornerShape(Dimens.MediumPadding.size),
    large = RoundedCornerShape(Dimens.LargePadding.size),
    extraLarge = RoundedCornerShape(Dimens.ExtraLargePadding.size)
)