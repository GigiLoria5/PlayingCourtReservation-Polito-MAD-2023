package it.polito.mad.g26.playingcourtreservation.util

import android.content.Context
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))