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
import ey.buriti.curral.model.ProducaoEntry
import ey.buriti.curral.model.ProductType
import ey.buriti.curral.ui.theme.CurralColors
import ey.buriti.curral.ui.viewmodel.ProducaoViewModel
import kotlin.math.abs
import kotlin.math.round
import org.koin.compose.viewmodel.koinViewModel

// ─── Data ──────────────────────────────────────────────────────────────────────

private data class ProductMetrics(
    val total: Double,
    val unit: String,
    val trendPercent: Double,
    val trendUp: Boolean,
    val bars: List<Float>,
    val labels: List<String>,
    val entryCount: Int,
)

private data class ProducaoResumo(
    val totalEntries: Int,
    val activeTypes: Int,
    val lastEntryDateLabel: String,
)

private val BarGreen = Color(0xFF2E7D32)
private val BarBlue = Color(0xFF1565C0)
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
    val leiteMetrics = remember(entries) { buildProductMetrics(entries, ProductType.LEITE, window = 7) }
    val ovosMetrics = remember(entries) { buildProductMetrics(entries, ProductType.OVOS, window = 14) }
    val melMetrics = remember(entries) { buildProductMetrics(entries, ProductType.MEL, window = 14) }
    val capimMetrics = remember(entries) { buildProductMetrics(entries, ProductType.CAPIM_FENO, window = 14) }
    val resumo = remember(entries) { buildResumo(entries) }

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
        item { LeiteCard(leiteMetrics) }

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
                    value = formatAmount(ovosMetrics.total),
                    unit = ovosMetrics.unit,
                    trend = formatPercent(ovosMetrics.trendPercent),
                    trendUp = ovosMetrics.trendUp,
                    bars = ovosMetrics.bars,
                    barColor = BarOrange,
                    modifier = Modifier.weight(1f),
                )
                SmallBarCard(
                    title = "Mel",
                    iconEmoji = "🍯",
                    iconBg = CurralColors.StatYellowBg,
                    value = formatAmount(melMetrics.total),
                    unit = melMetrics.unit,
                    trend = formatPercent(melMetrics.trendPercent),
                    trendUp = melMetrics.trendUp,
                    bars = melMetrics.bars,
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
                value = formatAmount(capimMetrics.total),
                unit = capimMetrics.unit,
                trend = formatPercent(capimMetrics.trendPercent),
                trendUp = capimMetrics.trendUp,
                bars = capimMetrics.bars,
                barColor = BarGreen,
            )
        }

        // ── Resumo (compact horizontal card) ───────────────────────────────────
        item { ResumoCard(resumo = resumo) }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

// ─── Leite Card ────────────────────────────────────────────────────────────────

@Composable
private fun LeiteCard(metrics: ProductMetrics) {
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
                    Text(
                        if (metrics.entryCount == 0) "Sem registros" else "Últimos ${metrics.bars.size} dias com registro",
                        fontSize = 12.sp,
                        color = CurralColors.TextSecondary,
                    )
                }
                TrendBadge(trend = formatPercent(metrics.trendPercent), up = metrics.trendUp)
            }

            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    formatAmount(metrics.total),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurralColors.TextPrimary,
                )
                Spacer(Modifier.width(6.dp))
                Text(metrics.unit, fontSize = 16.sp, color = CurralColors.TextSecondary, modifier = Modifier.padding(bottom = 5.dp))
            }

            Spacer(Modifier.height(12.dp))

            // Bar chart
            val maxVal = (metrics.bars.maxOrNull() ?: 0f).takeIf { it > 0f } ?: 1f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                metrics.bars.forEachIndexed { idx, value ->
                    val fraction = value / maxVal
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        Text(
                            "${formatAmount(value.toDouble())}${metrics.unit}",
                            fontSize = 9.sp,
                            color = CurralColors.TextSecondary,
                        )
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .fillMaxHeight(fraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(BarBlue),
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(metrics.labels.getOrElse(idx) { "--" }, fontSize = 10.sp, color = CurralColors.TextSecondary)
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

// ─── Resumo Card ───────────────────────────────────────────────────────────────

@Composable
private fun ResumoCard(resumo: ProducaoResumo) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CurralColors.Surface,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EmojiIconBox(emoji = "📈", bg = CurralColors.StatBlueBg)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Resumo da Produção", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CurralColors.TextPrimary)
                Text("${resumo.activeTypes} tipos com registro", fontSize = 12.sp, color = CurralColors.TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    resumo.totalEntries.toString(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CurralColors.TextPrimary,
                )
                Text(resumo.lastEntryDateLabel, fontSize = 12.sp, color = CurralColors.TextSecondary, textAlign = TextAlign.End)
            }
        }
    }
}

// ─── Mini Bar Chart ────────────────────────────────────────────────────────────

@Composable
private fun MiniBarChart(bars: List<Float>, barColor: Color, height: Dp) {
    val maxVal = (bars.maxOrNull() ?: 0f).takeIf { it > 0f } ?: 1f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        bars.forEach { value ->
            val fraction = (value / maxVal).coerceIn(0f, 1f)
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

private fun buildProductMetrics(
    entries: List<ProducaoEntry>,
    type: ProductType,
    window: Int,
): ProductMetrics {
    val typeEntries = entries.filter { it.productType == type }
    if (typeEntries.isEmpty()) {
        return ProductMetrics(
            total = 0.0,
            unit = defaultUnit(type),
            trendPercent = 0.0,
            trendUp = true,
            bars = List(window) { 0f },
            labels = List(window) { "--" },
            entryCount = 0,
        )
    }

    val byDate = linkedMapOf<String, Double>()
    typeEntries.sortedBy { it.date }.forEach { entry ->
        byDate[entry.date] = (byDate[entry.date] ?: 0.0) + entry.quantity
    }

    val dates = byDate.keys.toList()
    val recentDates = dates.takeLast(window)
    val previousDates = dates.dropLast(recentDates.size).takeLast(recentDates.size)

    val recentTotal = recentDates.sumOf { byDate[it] ?: 0.0 }
    val previousTotal = previousDates.sumOf { byDate[it] ?: 0.0 }
    val deltaPercent = when {
        previousTotal > 0 -> ((recentTotal - previousTotal) / previousTotal) * 100.0
        recentTotal > 0 -> 100.0
        else -> 0.0
    }

    val unit = typeEntries.lastOrNull()?.unit?.ifBlank { defaultUnit(type) } ?: defaultUnit(type)

    val bars = recentDates.map { (byDate[it] ?: 0.0).toFloat() }
    val labels = recentDates.map(::shortDateLabel)

    return ProductMetrics(
        total = recentTotal,
        unit = unit,
        trendPercent = abs(deltaPercent),
        trendUp = deltaPercent >= 0,
        bars = bars.ifEmpty { List(window) { 0f } },
        labels = labels.ifEmpty { List(window) { "--" } },
        entryCount = typeEntries.size,
    )
}

private fun buildResumo(entries: List<ProducaoEntry>): ProducaoResumo {
    val lastDate = entries.maxByOrNull { it.date }?.date
    return ProducaoResumo(
        totalEntries = entries.size,
        activeTypes = entries.map { it.productType }.toSet().size,
        lastEntryDateLabel = if (lastDate == null) "Sem registros" else "Último: ${formatDate(lastDate).take(5)}",
    )
}

private fun shortDateLabel(isoDate: String): String = formatDate(isoDate).take(5)

private fun defaultUnit(type: ProductType): String = when (type) {
    ProductType.LEITE -> "L"
    ProductType.OVOS -> "und"
    ProductType.MEL -> "kg"
    ProductType.CAPIM_FENO -> "kg"
}

private fun formatAmount(value: Double): String {
    val rounded = round(value * 10.0) / 10.0
    return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
}

private fun formatPercent(value: Double): String {
    val rounded = round(value * 10.0) / 10.0
    return if (rounded % 1.0 == 0.0) "${rounded.toInt()}%" else "${rounded}%"
}