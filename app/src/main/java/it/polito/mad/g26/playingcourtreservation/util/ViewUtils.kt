package it.polito.mad.g26.playingcourtreservation.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import java.io.ByteArrayOutputStream

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

fun View.setVisibility(isVisible: Boolean) {
    if (isVisible) makeVisible() else makeInvisible()
}

fun setupActionBar(activity: FragmentActivity?, title: String, enableBackButton: Boolean) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
    (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(enableBackButton)
}

@SuppressLint("RestrictedApi")
fun showActionBar(activity: FragmentActivity?) {
    val actionBar = (activity as? AppCompatActivity)?.supportActionBar
    actionBar?.setShowHideAnimationEnabled(false)
    actionBar?.show()
}

@SuppressLint("RestrictedApi")
fun hideActionBar(activity: FragmentActivity?) {
    val actionBar = (activity as? AppCompatActivity)?.supportActionBar
    actionBar?.setShowHideAnimationEnabled(false)
    actionBar?.hide()
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun ImageView.toByteArray(): ByteArray {
    val bitmap = (this.drawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    return baos.toByteArray()
}

fun ImageView.setImageFromByteArray(imageData: ByteArray) {
    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    setImageBitmap(bitmap)
}