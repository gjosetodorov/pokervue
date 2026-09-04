package com.casino.pokervue.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val Typography = Typography()

val WinPercentStyle = TextStyle(
    fontFamily = RajdhaniFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 76.sp,
    letterSpacing = (-0.02).em
)

val WinPercentStyleSmall = TextStyle(
    fontFamily = RajdhaniFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 68.sp,
    letterSpacing = (-0.02).em
)

val LabelStyle = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 9.5.sp,
    letterSpacing = 0.22.em
)

val LabelStyleLarge = TextStyle(
    fontFamily = SpaceGroteskFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.5.sp,
    letterSpacing = 0.22.em
)