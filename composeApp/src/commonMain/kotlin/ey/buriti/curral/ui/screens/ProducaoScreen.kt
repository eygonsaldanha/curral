package ey.buriti.curral.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.ProducaoViewModel
import org.koin.compose.viewmodel.koinViewModel

// ─── Data ──────────────────────────────────────────────────────────────────────

private data class ProducaoItem(
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val iconBg: Color,
    val value: String,
    val unit: String,
    val trend: String,
    val trendUp: Boolean,
    val barValues: List<Float>,
    val barColor: Color,
)

private val LeiteDayBars = listOf(220f, 235f, 245f, 210f, 250f, 195f, 180f)
private val LeiteDayLabels = listOf("Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom")
private val LeiteDayValueLabels = listOf("220L", "235L", "245L", "210L", "250L", "195L", "180L")
private val GreyDays = setOf(5, 6) // Sáb, Dom (future)

private val OvosBars = listOf(160f, 180f, 175f, 190f, 170f, 185f, 165f, 180f, 175f, 170f, 185f, 180f, 175f, 185f)
private val MelBars = listOf(40f, 45f, 48f, 42f, 50f, 47f, 44f, 46f, 52f, 49f, 45f, 48f, 46f, 50f)
private val CapimBars = listOf(80f, 90f, 85f, 95f, 88f, 92f, 86f, 91f, 87f, 89f, 94f, 88f, 90f, 85f)

private val BarGreen = Color(0xFF2E7D32)
private val BarGreenLight = Color(0xFFB0BEC5)
private val BarOrange = Color(0xFFF57C00)
private val BarYellow = Color(0xFFFFC107)
private val TrendGreen = Color(0xFF2E7D32)
private val TrendRed = Color(0xFFD32F2F)

@Composable
fun ProducaoScreen(
    modifier: Modifier = Modifier,
    vm: ProducaoViewModel = koinViewModel(),
) {
    val entries by vm.entries.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CurralColors.Background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                Text(
                    "Produção",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurralColors.TextPrimary,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
                Text(
                    if (entries.isEmpty()) "Nenhum registro recente" else "${entries.size} registros sincronizados",
                    fontSize = 12.sp,
                    color = CurralColors.TextSecondary,
                )
            }
        }

        // ── Produção de Leite (large card) ─────────────────────────────────────
        item { LeiteCard() }

        // ── Ovos + Mel (two small cards) ───────────────────────────────────────
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SmallBarCard(
                    title = "Ovos",
                    iconEmoji = "🥚",
                    iconBg = CurralColors.StatOrangeBg,
                    value = "1,260",
                    unit = "unidades",
                    trend = "-2.1%",
                    trendUp = false,
                    bars = OvosBars,
                    barColor = BarOrange,
                    modifier = Modifier.weight(1f),
                )
                SmallBarCard(
                    title = "Mel",
                    iconEmoji = "🍯",
                    iconBg = CurralColors.StatYellowBg,
                    value = "45",
                    unit = "kg",
                    trend = "+12.5%",
                    trendUp = true,
                    bars = MelBars,
                    barColor = BarYellow,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // ── Capim/Feno (medium card) ────────────────────────────────────────────
        item {
            MediumBarCard(
                title = "Capim/Feno",
                iconEmoji = "🌿",
                iconBg = CurralColors.StatusHealthyBg,
                value = "850",
                unit = "kg",
                trend = "+5%",
                trendUp = true,
                bars = CapimBars,
                barColor = BarGreen,
            )
        }

        // ── Consumo de Água (compact horizontal card) ──────────────────────────
        item { AguaCard() }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─── Leite Card ────────────────────────────────────────────────────────────────

@Composable
private fun LeiteCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EmojiIconBox(emoji = "🥛", bg = CurralColors.StatBlueBg)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Produção de Leite", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                    Text("Esta semana", fontSize = 12.sp, color = CurralColors.TextSecondary)
                }
                TrendBadge(trend = "+5.2%", up = true)
            }

            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("1,535", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                Spacer(Modifier.width(6.dp))
                Text("litros", fontSize = 16.sp, color = CurralColors.TextSecondary, modifier = Modifier.padding(bottom = 5.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Bar chart
            val maxVal = LeiteDayBars.max()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                LeiteDayBars.forEachIndexed { idx, value ->
                    val fraction = value / maxVal
                    val isGrey = idx in GreyDays
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        Text(
                            LeiteDayValueLabels[idx],
                            fontSize = 9.sp,
                            color = if (isGrey) CurralColors.TextSecondary else CurralColors.TextSecondary,
                        )
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(if (isGrey) BarGreenLight else BarGreen),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(LeiteDayLabels[idx], fontSize = 10.sp, color = CurralColors.TextSecondary)
                    }
                }
            }
        }
    }
}

// ─── Small Bar Card ────────────────────────────────────────────────────────────

@Composable
private fun SmallBarCard(
    title: String,
    iconEmoji: String,
    iconBg: Color,
    value: String,
    unit: String,
    trend: String,
    trendUp: Boolean,
    bars: List<Float>,
    barColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                EmojiIconBox(emoji = iconEmoji, bg = iconBg, size = 32.dp, fontSize = 16)
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
            }
            Spacer(Modifier.height(10.dp))
            MiniBarChart(bars = bars, barColor = barColor, height = 44.dp)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                Spacer(Modifier.width(4.dp))
                Text(unit, fontSize = 11.sp, color = CurralColors.TextSecondary, modifier = Modifier.padding(bottom = 2.dp))
            }
            Spacer(Modifier.height(2.dp))
            TrendBadge(trend = trend, up = trendUp)
        }
    }
}

// ─── Medium Bar Card ───────────────────────────────────────────────────────────

@Composable
private fun MediumBarCard(
    title: String,
    iconEmoji: String,
    iconBg: Color,
    value: String,
    unit: String,
    trend: String,
    trendUp: Boolean,
    bars: List<Float>,
    barColor: Color,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EmojiIconBox(emoji = iconEmoji, bg = iconBg, size = 32.dp, fontSize = 16)
                Spacer(Modifier.width(8.dp))
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
            }
            Spacer(Modifier.height(10.dp))
            MiniBarChart(bars = bars, barColor = barColor, height = 44.dp)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                    Spacer(Modifier.width(4.dp))
                    Text(unit, fontSize = 12.sp, color = CurralColors.TextSecondary, modifier = Modifier.padding(bottom = 3.dp))
                }
                TrendBadge(trend = trend, up = trendUp)
            }
        }
    }
}

// ─── Água Card ─────────────────────────────────────────────────────────────────

@Composable
private fun AguaCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EmojiIconBox(emoji = "💧", bg = CurralColors.StatBlueBg)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Consumo de Água", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                Text("Esta semana", fontSize = 12.sp, color = CurralColors.TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("3,200", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                Text("litros", fontSize = 12.sp, color = CurralColors.TextSecondary, textAlign = TextAlign.End)
            }
        }
    }
}

// ─── Mini Bar Chart ────────────────────────────────────────────────────────────

@Composable
private fun MiniBarChart(bars: List<Float>, barColor: Color, height: Dp) {
    val maxVal = bars.max()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        bars.forEach { value ->
            val fraction = value / maxVal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(fraction)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(barColor),
            )
        }
    }
}

// ─── Trend Badge ───────────────────────────────────────────────────────────────

@Composable
private fun TrendBadge(trend: String, up: Boolean) {
    val color = if (up) TrendGreen else TrendRed
    Text(
        text = if (up) "↗ $trend" else "↘ $trend",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = color,
    )
}

// ─── Emoji Icon Box ────────────────────────────────────────────────────────────

@Composable
private fun EmojiIconBox(
    emoji: String,
    bg: Color,
    size: Dp = 40.dp,
    fontSize: Int = 20,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(10.dp))
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(emoji, fontSize = fontSize.sp)
    }
}