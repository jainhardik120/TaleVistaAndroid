package com.jainhardik120.talevista.util

import android.os.Build
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

const val BASE_SERVER_URL = "https://tale-vista-server.onrender.com"

fun timeAgoText(text: String): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    try {
        val date: Date? = sdf.parse(text)
        if (date != null) {
            val timeInMillis: Long = date.time
            val currentTimeInMillis = System.currentTimeMillis()
            return DateUtils.getRelativeTimeSpanString(
                timeInMillis,
                currentTimeInMillis,
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val currentDateTime = LocalDateTime.now()
        val currentZone = ZoneId.systemDefault()
        val dateTime = LocalDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME)
        val gmtZone = ZoneId.of("GMT")

        val difference = Duration.between(
            dateTime.atZone(gmtZone).toInstant(),
            currentDateTime.atZone(currentZone).toInstant()
        )
        val seconds = difference.seconds
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7

        when {
            weeks > 0 -> "$weeks week${if (weeks > 1) "s" else ""} ago"
            days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
            hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            else -> "$seconds second${if (seconds > 1) "s" else ""} ago"
        }
    } else {
        text
    }
}