package com.example.quicknbiteapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.quicknbiteapp.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Poppins = FontFamily(
    Font(R.font.poppins_extrabold)
)

val LeagueSpartan = FontFamily(
    Font(R.font.leaguespartan_light),
    Font(R.font.leaguespartan_medium),
    Font(R.font.leaguespartan_regular),
    Font(R.font.leaguespartan_bold),
    Font(R.font.leaguespartan_semibold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp
    ),
    displayMedium = TextStyle(
        fontFamily = LeagueSpartan,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = LeagueSpartan,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = LeagueSpartan,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = LeagueSpartan,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
)