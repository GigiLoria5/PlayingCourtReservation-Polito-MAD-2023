package it.polito.mad.g26.playingcourtreservation.util

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

fun View.setVisibility(isVisible: Boolean) {
    if (isVisible) makeVisible() else makeInVisible()
}

fun setupActionBar(activity: FragmentActivity?, title: String, enableBackButton: Boolean) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
    (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(enableBackButton)
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}