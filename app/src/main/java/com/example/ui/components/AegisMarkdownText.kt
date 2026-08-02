package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

sealed class MarkdownBlock {
    data class Header(val level: Int, val content: String) : MarkdownBlock()
    data class Code(val language: String, val code: String) : MarkdownBlock()
    data class ListItem(val isOrdered: Boolean, val index: Int, val content: String) : MarkdownBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownBlock()
    data class Paragraph(val content: String) : MarkdownBlock()
}

@Composable
fun AegisMarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    textColor: Color = AegisTextPrimary
) {
    val blocks = remember(markdown) { parseMarkdown(markdown) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.Header -> RenderHeader(block, textColor)
                is MarkdownBlock.Code -> RenderCodeBlock(block)
                is MarkdownBlock.ListItem -> RenderListItem(block, textColor)
                is MarkdownBlock.Table -> RenderTable(block)
                is MarkdownBlock.Paragraph -> RenderParagraph(block.content, textColor)
            }
        }
    }
}

@Composable
private fun RenderHeader(header: MarkdownBlock.Header, defaultTextColor: Color) {
    val style = when (header.level) {
        1 -> MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = AegisGoldPrimary,
            fontSize = 20.sp
        )
        2 -> MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = AegisCyanAccent,
            fontSize = 17.sp
        )
        else -> MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = defaultTextColor,
            fontSize = 15.sp
        )
    }

    Text(
        text = buildInlineMarkdown(header.content, defaultTextColor),
        style = style,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun RenderParagraph(content: String, defaultTextColor: Color) {
    Text(
        text = buildInlineMarkdown(content, defaultTextColor),
        style = MaterialTheme.typography.bodyMedium,
        color = defaultTextColor,
        lineHeight = 20.sp
    )
}

@Composable
private fun RenderListItem(item: MarkdownBlock.ListItem, defaultTextColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = if (item.isOrdered) "${item.index}. " else "• ",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = AegisGoldPrimary,
            modifier = Modifier.width(22.dp)
        )
        Text(
            text = buildInlineMarkdown(item.content, defaultTextColor),
            style = MaterialTheme.typography.bodyMedium,
            color = defaultTextColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RenderCodeBlock(codeBlock: MarkdownBlock.Code) {
    val context = LocalContext.current
    val languageDisplay = codeBlock.language.ifBlank { "CODE" }.uppercase()

    Surface(
        color = AegisDarkCanvas,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("code_block_surface")
    ) {
        Column {
            // Code block header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AegisSurfaceDark)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = languageDisplay,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = AegisCyanAccent
                )

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Code snippet", codeBlock.code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Code",
                        tint = AegisTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            HorizontalDivider(color = AegisBorderDark)

            // Code content with horizontal scroll
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = codeBlock.code,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = AegisTextPrimary,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun RenderTable(table: MarkdownBlock.Table) {
    Surface(
        color = AegisSurfaceDark,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AegisBorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // Headers
            Row(
                modifier = Modifier
                    .background(AegisSurfaceVariantDark)
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                table.headers.forEach { header ->
                    Text(
                        text = header.trim(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = AegisGoldPrimary,
                        modifier = Modifier
                            .widthIn(min = 90.dp)
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            HorizontalDivider(color = AegisBorderDark, modifier = Modifier.padding(vertical = 4.dp))

            // Rows
            table.rows.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (rowIndex % 2 == 0) Color.Transparent else AegisSurfaceVariantDark.copy(alpha = 0.3f))
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    row.forEach { cell ->
                        Text(
                            text = cell.trim(),
                            style = MaterialTheme.typography.bodySmall,
                            color = AegisTextPrimary,
                            modifier = Modifier
                                .widthIn(min = 90.dp)
                                .padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Builds AnnotatedString for inline markdown elements (bold, italic, inline code)
 */
private fun buildInlineMarkdown(text: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val length = text.length

        while (cursor < length) {
            when {
                // Bold: **text** or __text__
                (text.startsWith("**", cursor) || text.startsWith("__", cursor)) && length >= cursor + 4 -> {
                    val delimiter = text.substring(cursor, cursor + 2)
                    val end = text.indexOf(delimiter, cursor + 2)
                    if (end != -1) {
                        val inner = text.substring(cursor + 2, end)
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(inner)
                        }
                        cursor = end + 2
                    } else {
                        append(text[cursor])
                        cursor++
                    }
                }
                // Italic: *text* or _text_
                (text[cursor] == '*' || text[cursor] == '_') && cursor + 1 < length && text[cursor + 1] != ' ' -> {
                    val delimiter = text[cursor]
                    val end = text.indexOf(delimiter, cursor + 1)
                    if (end != -1 && end > cursor + 1) {
                        val inner = text.substring(cursor + 1, end)
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(inner)
                        }
                        cursor = end + 1
                    } else {
                        append(text[cursor])
                        cursor++
                    }
                }
                // Inline code: `code`
                text[cursor] == '`' -> {
                    val end = text.indexOf('`', cursor + 1)
                    if (end != -1) {
                        val codeInner = text.substring(cursor + 1, end)
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = AegisBorderDark,
                                color = AegisCyanAccent,
                                fontSize = 13.sp
                            )
                        ) {
                            append(" $codeInner ")
                        }
                        cursor = end + 1
                    } else {
                        append(text[cursor])
                        cursor++
                    }
                }
                else -> {
                    append(text[cursor])
                    cursor++
                }
            }
        }
    }
}

/**
 * Parses markdown string into a list of structured blocks
 */
private fun parseMarkdown(markdown: String): List<MarkdownBlock> {
    val lines = markdown.lines()
    val blocks = mutableListOf<MarkdownBlock>()

    var i = 0
    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()

        when {
            // Code block start ```
            trimmed.startsWith("```") -> {
                val language = trimmed.removePrefix("```").trim()
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].trim().startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                blocks.add(MarkdownBlock.Code(language, codeLines.joinToString("\n")))
                i++
            }
            // Headers
            trimmed.startsWith("# ") -> {
                blocks.add(MarkdownBlock.Header(1, trimmed.removePrefix("# ").trim()))
                i++
            }
            trimmed.startsWith("## ") -> {
                blocks.add(MarkdownBlock.Header(2, trimmed.removePrefix("## ").trim()))
                i++
            }
            trimmed.startsWith("### ") -> {
                blocks.add(MarkdownBlock.Header(3, trimmed.removePrefix("### ").trim()))
                i++
            }
            // Unordered list
            trimmed.startsWith("* ") || trimmed.startsWith("- ") -> {
                val content = if (trimmed.startsWith("* ")) trimmed.removePrefix("* ") else trimmed.removePrefix("- ")
                blocks.add(MarkdownBlock.ListItem(isOrdered = false, index = 0, content = content.trim()))
                i++
            }
            // Ordered list e.g. "1. "
            trimmed.matches(Regex("^\\d+\\.\\s+.*")) -> {
                val match = Regex("^(\\d+)\\.\\s+(.*)").find(trimmed)
                if (match != null) {
                    val index = match.groupValues[1].toIntOrNull() ?: 1
                    val content = match.groupValues[2]
                    blocks.add(MarkdownBlock.ListItem(isOrdered = true, index = index, content = content.trim()))
                } else {
                    blocks.add(MarkdownBlock.Paragraph(line))
                }
                i++
            }
            // Markdown Table e.g. | Header 1 | Header 2 |
            trimmed.startsWith("|") && trimmed.endsWith("|") -> {
                val tableHeaders = trimmed.split("|").filter { it.isNotBlank() }
                i++
                // Skip separator row |---|---|
                if (i < lines.size && lines[i].trim().startsWith("|") && lines[i].contains("---")) {
                    i++
                }
                val tableRows = mutableListOf<List<String>>()
                while (i < lines.size && lines[i].trim().startsWith("|") && lines[i].trim().endsWith("|")) {
                    val rowCells = lines[i].trim().split("|").filter { it.isNotBlank() }
                    tableRows.add(rowCells)
                    i++
                }
                blocks.add(MarkdownBlock.Table(tableHeaders, tableRows))
            }
            // Blank lines
            trimmed.isBlank() -> {
                i++
            }
            // Default paragraph
            else -> {
                blocks.add(MarkdownBlock.Paragraph(line))
                i++
            }
        }
    }

    return blocks
}
