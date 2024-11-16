package com.fm404.onair.core.designsystem.theme

import androidx.compose.material3.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val pExtraBold = FontFamily(Font(com.fm404.onair.core.common.R.font.pextrabold))
val pBold = FontFamily(Font(com.fm404.onair.core.common.R.font.pbold))
val pSemiBold = FontFamily(Font(com.fm404.onair.core.common.R.font.psemibold))
val pMedium = FontFamily(Font(com.fm404.onair.core.common.R.font.pmedium))
val pRegular = FontFamily(Font(com.fm404.onair.core.common.R.font.pregular))
val pLight = FontFamily(Font(com.fm404.onair.core.common.R.font.plight))
val pExtraLight = FontFamily(Font(com.fm404.onair.core.common.R.font.pextralight))
val pThin = FontFamily(Font(com.fm404.onair.core.common.R.font.pthin))
val pBlack = FontFamily(Font(com.fm404.onair.core.common.R.font.pblack))
//val pBold = FontFamily(Font(com.fm404.onair.core.common.R.font.pbold)

val Typography = Typography(
    // 큰 제목
    headlineLarge = TextStyle(
        fontFamily = pExtraBold,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // 일반 제목
    titleLarge = TextStyle(
        fontFamily = pBold,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // 본문 텍스트
    bodyLarge = TextStyle(
        fontFamily = pMedium,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // 작은 텍스트
    bodySmall = TextStyle(
        fontFamily = pRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
)