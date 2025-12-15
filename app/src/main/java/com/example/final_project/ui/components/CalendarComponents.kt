package com.example.final_project.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch

class CalendarState(
    initialDate: Date = Date()
) {
    private val calendar = Calendar.getInstance()

    var visibleMonth: Calendar by mutableStateOf(
        (Calendar.getInstance().apply { time = initialDate }).also {
            it.set(Calendar.DAY_OF_MONTH, 1)
            normalizeToStartOfDay(it)
        }
    )

    var selectedDateMillis by mutableStateOf(normalizeDate(initialDate).time)

    fun showPreviousMonth() {
        visibleMonth = (visibleMonth.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
    }

    fun showNextMonth() {
        visibleMonth = (visibleMonth.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
    }

    private fun normalizeDate(date: Date): Date {
        val cal = Calendar.getInstance().apply {
            time = date
            normalizeToStartOfDay(this)
        }
        return cal.time
    }

    private fun normalizeToStartOfDay(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

}

@Composable
fun rememberCalendarState(initialDate: Date = Date()): CalendarState {
    return remember { CalendarState(initialDate) }
}


@Composable
fun CalendarDay(
    day: Day,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    indicator: @Composable () -> Unit = {}
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { day.onClick(day.date) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.dayOfWeek,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )
            Text(
                text = day.dayOfMonth,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = contentColor
            )
            Box(modifier = Modifier.height(8.dp)) {
                indicator()
            }
        }
    }
}

@Composable
fun MonthCalendar(
    state: CalendarState,
    modifier: Modifier = Modifier,
    dayContent: @Composable (day: Calendar, isSelected: Boolean) -> Unit
) {
    val monthCalendar = state.visibleMonth
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(monthCalendar.time)

    val firstDayOfWeek = monthCalendar.firstDayOfWeek
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = (monthCalendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
    val startOffset = (firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek + 7) % 7

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { state.showPreviousMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
            }
            Text(
                text = monthName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            IconButton(onClick = { state.showNextMonth() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            val days = (0..6).map { (firstDayOfWeek + it -1) % 7 + 1}
            for (day in days) {
                Text(
                    text = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, day) }
                        .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())!!,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar days
        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (day in 0..6) {
                    val dayIndex = week * 7 + day
                    if (dayIndex >= startOffset && dayIndex < startOffset + daysInMonth) {
                        val dayOfMonth = dayIndex - startOffset + 1
                        val currentDate = (monthCalendar.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, dayOfMonth) }
                        val isSelected = state.selectedDateMillis == currentDate.timeInMillis
                        dayContent(currentDate, isSelected)
                    } else {
                        Spacer(modifier = Modifier.weight(1f)) // Empty cell
                    }
                }
            }
        }
    }
}

data class Day(
    val date: Date,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val onClick: (Date) -> Unit
)
