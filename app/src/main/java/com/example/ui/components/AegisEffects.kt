package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AegisDomain
import com.example.ui.theme.*
import kotlin.math.sin

/**
 * Tactical glowing grid background canvas with subtle ambient neon pulse
 */
@Composable
fun AegisBackgroundCanvas(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "BackgroundPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Subtle radial ambient glow at top-left (Gold) and bottom-right (Cyan)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AegisGoldPrimary.copy(alpha = pulseAlpha * 0.7f), Color.Transparent),
                center = Offset(width * 0.2f, height * 0.1f),
                radius = width * 0.6f
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(AegisCyanAccent.copy(alpha = pulseAlpha * 0.8f), Color.Transparent),
                center = Offset(width * 0.85f, height * 0.85f),
                radius = width * 0.7f
            )
        )

        // 2. Cybernetic grid lines (subtle 40dp spacing)
        val gridSize = 48.dp.toPx()
        val gridColor = AegisBorderDark.copy(alpha = 0.25f)

        var x = 0f
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridSize
        }

        var y = 0f
        while (y < height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSize
        }
    }
}

/**
 * Animated Pulse Dot for ACTIVE status indicators
 */
@Composable
fun PulsingStatusDot(
    color: Color = AegisSecurityGreen,
    sizeDp: Int = 10
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StatusDotPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size((sizeDp * 1.6).dp)) {
        Box(
            modifier = Modifier
                .size((sizeDp * scale).dp)
                .clip(CircleShape)
                .background(color.copy(alpha = alpha * 0.4f))
        )
        Box(
            modifier = Modifier
                .size(sizeDp.dp)
                .clip(CircleShape)
                .background(color)
        )
    }
}

/**
 * Voice Input Audio Waveform Visualizer
 */
@Composable
fun AudioWaveformVisualizer(
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isListening) return

    val infiniteTransition = rememberInfiniteTransition(label = "AudioWave")
    val barPhases = List(7) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(350 + index * 90, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AegisSurfaceVariantDark)
            .border(1.dp, AegisAlertRed.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PulsingStatusDot(color = AegisAlertRed, sizeDp = 8)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Voice Directive Listening...",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = AegisAlertRed
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            barPhases.forEach { animatedHeight ->
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height((12 + animatedHeight.value * 18).dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(AegisAlertRed, AegisGoldPrimary)
                            )
                        )
                )
            }
        }
    }
}

/**
 * Neural Core Synthesizing Laser Scanning Indicator
 */
@Composable
fun NeuralProcessingIndicator(
    isProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isProcessing,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "LaserScan")
        val progress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "laserProgress"
        )

        Surface(
            color = AegisSurfaceDark,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AegisCyanAccent.copy(alpha = 0.6f)),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("neural_processing_indicator")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = AegisCyanAccent,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "AEGIS Neural Core: Synthesizing Response...",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = AegisCyanAccent
                        )
                    }

                    Text(
                        text = "Gemini 3.6 • Parallel Search",
                        style = MaterialTheme.typography.labelSmall,
                        color = AegisGoldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Laser scanning line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(AegisBorderDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.3f)
                            .fillMaxHeight()
                            .offset(x = (progress * 240).dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, AegisCyanAccent, AegisGoldPrimary, Color.Transparent)
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * Quick Suggestion Directive Chips tailored per domain
 */
@Composable
fun QuickPromptSuggestions(
    selectedDomain: AegisDomain,
    onPromptSelected: (String) -> Unit
) {
    val suggestions = remember(selectedDomain) {
        when (selectedDomain) {
            AegisDomain.SECURITY -> listOf(
                "🛡️ Perform System Security Audit",
                "🔒 Verify End-to-End Encryption",
                "⚡ Analyze Vulnerability Surface",
                "🔑 Audit Executive Auth Clearance"
            )
            AegisDomain.DATA_ANALYSIS -> listOf(
                "📊 Analyze Revenue & Growth Metrics",
                "📈 Forecast Q3 Market Performance",
                "🔍 Detect System Log Anomalies",
                "⚡ Generate Data Visualization Schema"
            )
            AegisDomain.MATH_SCIENCE -> listOf(
                "🧮 Solve Differential Wave Equation",
                "⚛️ Derive Quantum State Transition",
                "📐 Perform Matrix Eigen-Decomposition",
                "📊 Calculate Statistical Confidence Interval"
            )
            AegisDomain.ART_CREATIVE -> listOf(
                "🎨 Generate Cyberpunk Executive Concept",
                "🖌️ Compose Modern Minimal Palette",
                "✨ Craft Design System Spec",
                "🖼️ Create 3D Holographic UI Render Prompt"
            )
            AegisDomain.SALES_ENTERPRISE -> listOf(
                "💰 Enterprise Pipeline Forecast",
                "🤝 Executive Client Proposal Audit",
                "🎯 Lead Scoring Matrix & Strategy",
                "📊 Quarterly SLA Compliance Brief"
            )
            AegisDomain.HEALTH_WELLNESS -> listOf(
                "🌿 Optimize Executive Circadian Rhythm",
                "💤 Sleep Waveform & Recovery Score",
                "🏃 Daily Biometric Vital Targets",
                "🧘 High-Performance Stress Reduction"
            )
            AegisDomain.EXECUTIVE -> listOf(
                "📅 Prioritize Top 3 Quarterly OKRs",
                "⏱️ Executive Schedule Optimization",
                "📋 Summarize Active Task Backlog",
                "🚀 Launch Strategic Roadmap Briefing"
            )
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { promptText ->
            Surface(
                color = AegisSurfaceDark,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorderDark),
                modifier = Modifier
                    .clickable { onPromptSelected(promptText.substringAfter(" ")) }
                    .testTag("suggestion_chip_${promptText.take(10)}")
            ) {
                Text(
                    text = promptText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = AegisTextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
