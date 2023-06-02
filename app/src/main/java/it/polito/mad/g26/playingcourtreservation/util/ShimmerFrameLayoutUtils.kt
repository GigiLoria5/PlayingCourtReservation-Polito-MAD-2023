package it.polito.mad.g26.playingcourtreservation.util

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout


fun ShimmerFrameLayout.startShimmerRVAnimation(recyclerView: RecyclerView) {
    recyclerView.makeInvisible()
    makeVisible()
    startShimmer()
}

fun ShimmerFrameLayout.stopShimmerRVAnimation(recyclerView: RecyclerView) {
    stopShimmer()
    makeInvisible()
    recyclerView.makeVisible()
}

fun ShimmerFrameLayout.startShimmerImgAnimation(image: ImageView) {
    image.makeInvisible()
    makeVisible()
    startShimmer()
}

fun ShimmerFrameLayout.stopShimmerImgAnimation(image: ImageView) {
    stopShimmer()
    makeInvisible()
    image.makeVisible()
}

fun ShimmerFrameLayout.startShimmerTextAnimation(text: TextView) {
    text.makeInvisible()
    makeVisible()
    startShimmer()
}

fun ShimmerFrameLayout.stopShimmerTextAnimation(text: TextView) {
    stopShimmer()
    makeInvisible()
    text.makeVisible()
}

fun ShimmerFrameLayout.startShimmerTVListAnimation(tvList: List<TextView>) {
    tvList.forEach { it.makeInvisible() }
    makeVisible()
    startShimmer()
}

fun ShimmerFrameLayout.stopShimmerTVListAnimation(tvList: List<TextView>) {
    stopShimmer()
    makeInvisible()
    tvList.forEach { it.makeVisible() }
}