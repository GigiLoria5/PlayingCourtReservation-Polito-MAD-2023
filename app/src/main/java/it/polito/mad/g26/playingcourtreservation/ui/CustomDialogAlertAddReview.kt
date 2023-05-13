package it.polito.mad.g26.playingcourtreservation.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import it.polito.mad.g26.playingcourtreservation.R

class CustomDialogAlertAddReview constructor(context: Context?, reservationId: Int, userId: Int) : AlertDialog(context!!) {
    init {
        initialize()
    }

    private fun initialize() {
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_dialog_add_review, null)
        setView(view)
        val builder = Builder(context)
        builder.setView(view)
        val rating = view.findViewById<RatingBar>(R.id.rating)
        val review = view.findViewById<EditText>(R.id.et_review)
        val submit = view.findViewById<Button>(R.id.submit_button)
        val cancel = view.findViewById<Button>(R.id.cancel_button)

        submit.setOnClickListener {
            println("${rating.rating}")
            println("$review")
            if (review.length() < 10)
                review.error = "Review size should be at least 10 characters long"
            else{
                println("good review")

            }
        }

        cancel.setOnClickListener {
            this.dismiss()
        }
    }
}