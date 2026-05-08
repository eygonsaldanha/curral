package ey.buriti.curral.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class CurralColorScheme(
    val Background: Color,
    val Surface: Color,

    // Alerts
    val AlertBackground: Color,
    val AlertItemBackground: Color,
    val AlertAccent: Color,
    val AlertDot: Color,

    // Stats
    val StatBlue: Color,
    val StatBlueBg: Color,
    val StatOrange: Color,
    val StatOrangeBg: Color,
    val StatYellowBg: Color,

    // Navigation
    val FabGreen: Color,
    val NavActive: Color,
    val NavInactive: Color,

    // Text
    val TextPrimary: Color,
    val TextSecondary: Color,

    // Calendar
    val CalendarToday: Color,

    // Search
    val SearchBackground: Color,

    // Animal status
    val StatusHealthy: Color,
    val StatusHealthyBg: Color,
    val StatusDead: Color,
    val StatusDeadBg: Color,
    val StatusSold: Color,
    val StatusSoldBg: Color,
    val StatusSick: Color,
    val StatusSickBg: Color,
    val StatusPregnant: Color,
    val StatusPregnantBg: Color,

    // Filter chips
    val FilterChipActiveBg: Color,
    val FilterChipActiveText: Color,
    val FilterChipBg: Color,
    val FilterChipText: Color,

    // Section divider / card
    val CardSurface: Color,
    val SectionHeader: Color,

    // Animal sex badges
    val SexMaleBg: Color,
    val SexMaleFg: Color,
    val SexFemaleBg: Color,
    val SexFemaleFg: Color,

    // Gestation
    val GestationPurpleBg: Color,

    // Estoque category backgrounds
    val CategoryRacaoBg: Color,
    val CategoryFerramentasBg: Color,
    val CategoryRemediosBg: Color,
    val CategoryOutrosBg: Color,
)

val LightCurralColors = CurralColorScheme(
    Background = Color(0xFFF5F5F5),
    Surface = Color.White,
    AlertBackground = Color(0xFFFEECDB),
    AlertItemBackground = Color(0xFFFFF4E8),
    AlertAccent = Color(0xFFE65100),
    AlertDot = Color(0xFFE53935),
    StatBlue = Color(0xFF2196F3),
    StatBlueBg = Color(0xFFE3F2FD),
    StatOrange = Color(0xFFFF9800),
    StatOrangeBg = Color(0xFFFFF3E0),
    StatYellowBg = Color(0xFFFFFDE7),
    FabGreen = Color(0xFF7CB342),
    NavActive = Color(0xFF212121),
    NavInactive = Color(0xFF9E9E9E),
    TextPrimary = Color(0xFF212121),
    TextSecondary = Color(0xFF757575),
    CalendarToday = Color(0xFF7CB342),
    SearchBackground = Color(0xFFEEEEEE),
    StatusHealthy = Color(0xFF4CAF50),
    StatusHealthyBg = Color(0xFFE8F5E9),
    StatusDead = Color(0xFF9E9E9E),
    StatusDeadBg = Color(0xFFF5F5F5),
    StatusSold = Color(0xFF2196F3),
    StatusSoldBg = Color(0xFFE3F2FD),
    StatusSick = Color(0xFFFF5722),
    StatusSickBg = Color(0xFFFBE9E7),
    StatusPregnant = Color(0xFFAB47BC),
    StatusPregnantBg = Color(0xFFF3E5F5),
    FilterChipActiveBg = Color(0xFF212121),
    FilterChipActiveText = Color.White,
    FilterChipBg = Color(0xFFEEEEEE),
    FilterChipText = Color(0xFF424242),
    CardSurface = Color.White,
    SectionHeader = Color(0xFF424242),
    SexMaleBg = Color(0xFFE3F2FD),
    SexMaleFg = Color(0xFF1565C0),
    SexFemaleBg = Color(0xFFF3E5F5),
    SexFemaleFg = Color(0xFF9C27B0),
    GestationPurpleBg = Color(0xFFF5F0FF),
    CategoryRacaoBg = Color(0xFFE8F5E9),
    CategoryFerramentasBg = Color(0xFFE3F2FD),
    CategoryRemediosBg = Color(0xFFF3E5F5),
    CategoryOutrosBg = Color(0xFFFFF3E0),
)

val DarkCurralColors = CurralColorScheme(
    Background = Color(0xFF121212),
    Surface = Color(0xFF1E1E1E),
    AlertBackground = Color(0xFF3A1800),
    AlertItemBackground = Color(0xFF2A1200),
    AlertAccent = Color(0xFFFF8A50),
    AlertDot = Color(0xFFEF5350),
    StatBlue = Color(0xFF42A5F5),
    StatBlueBg = Color(0xFF0D2744),
    StatOrange = Color(0xFFFFA726),
    StatOrangeBg = Color(0xFF2D1B00),
    StatYellowBg = Color(0xFF2A2000),
    FabGreen = Color(0xFF7CB342),
    NavActive = Color(0xFFEEEEEE),
    NavInactive = Color(0xFF616161),
    TextPrimary = Color(0xFFEEEEEE),
    TextSecondary = Color(0xFF9E9E9E),
    CalendarToday = Color(0xFF7CB342),
    SearchBackground = Color(0xFF2C2C2C),
    StatusHealthy = Color(0xFF66BB6A),
    StatusHealthyBg = Color(0xFF1B3A1C),
    StatusDead = Color(0xFF757575),
    StatusDeadBg = Color(0xFF2A2A2A),
    StatusSold = Color(0xFF42A5F5),
    StatusSoldBg = Color(0xFF0D2744),
    StatusSick = Color(0xFFFF7043),
    StatusSickBg = Color(0xFF3A1400),
    StatusPregnant = Color(0xFFCE93D8),
    StatusPregnantBg = Color(0xFF2A103A),
    FilterChipActiveBg = Color(0xFFE0E0E0),
    FilterChipActiveText = Color(0xFF212121),
    FilterChipBg = Color(0xFF2C2C2C),
    FilterChipText = Color(0xFFBDBDBD),
    CardSurface = Color(0xFF1E1E1E),
    SectionHeader = Color(0xFFE0E0E0),
    SexMaleBg = Color(0xFF0D2744),
    SexMaleFg = Color(0xFF42A5F5),
    SexFemaleBg = Color(0xFF2A103A),
    SexFemaleFg = Color(0xFFCE93D8),
    GestationPurpleBg = Color(0xFF1E0D30),
    CategoryRacaoBg = Color(0xFF1B3A1C),
    CategoryFerramentasBg = Color(0xFF0D2744),
    CategoryRemediosBg = Color(0xFF2A103A),
    CategoryOutrosBg = Color(0xFF2D1B00),
)

val LocalCurralColors = compositionLocalOf { LightCurralColors }

@Suppress("ComposableNaming")
val CurralColors: CurralColorScheme
    @Composable get() = LocalCurralColors.current

