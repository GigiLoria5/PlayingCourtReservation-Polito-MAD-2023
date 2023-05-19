package it.polito.mad.g26.playingcourtreservation.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout

fun ShimmerFrameLayout.makeVisible() {
    visibility = View.VISIBLE
}

fun ShimmerFrameLayout.makeInvisible() {
    visibility = View.INVISIBLE
}

fun ShimmerFrameLayout.startShimmerAnimation(recyclerView: RecyclerView) {
    recyclerView.makeInvisible()
    makeVisible()
    startShimmer()
}

fun ShimmerFrameLayout.stopShimmerAnimation(recyclerView: RecyclerView) {
    stopShimmer()
    makeInvisible()
    recyclerView.makeVisible()
}