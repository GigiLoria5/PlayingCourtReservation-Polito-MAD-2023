package it.polito.mad.g26.playingcourtreservation.util

import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.isItemChecked(itemId: Int): Boolean {
    return menu.findItem(itemId).isChecked
}

fun BottomNavigationView.setCheckedMenuItem(itemId: Int) {
    menu.findItem(itemId).isChecked = true
    makeVisible()
}

fun BottomNavigationView.makeVisible() {
    visibility = View.VISIBLE
}

fun BottomNavigationView.makeGone() {
    visibility = View.GONE
}
