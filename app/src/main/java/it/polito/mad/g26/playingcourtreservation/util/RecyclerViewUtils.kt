package it.polito.mad.g26.playingcourtreservation.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.makeVisible() {
    visibility = View.VISIBLE
}

fun RecyclerView.makeInvisible() {
    visibility = View.INVISIBLE
}

fun RecyclerView.makeGone() {
    visibility = View.GONE
}