package it.polito.mad.g26.playingcourtreservation.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import it.polito.mad.g26.playingcourtreservation.R

class CustomDialogAlertAddReview: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context.let {
            val view: View = LayoutInflater.from(context).inflate(R.layout.custom_dialog_add_review, null)
            val builder = AlertDialog.Builder(it!!)
            builder.setView(view)
            val rating = view.findViewById<RatingBar>(R.id.rating)
            val textReview = view.findViewById<EditText>(R.id.et_review)
            val submit = view.findViewById<Button>(R.id.submit_button)
            val cancel = view.findViewById<Button>(R.id.cancel_button)

            submit.setOnClickListener {
                println("${rating.rating}")
                println("$textReview")
                if (textReview.length() < 10)
                    textReview.error = "Review size should be at least 10 characters long"
                else{
                    println("good review")
                }
            }

            cancel.setOnClickListener {
                this.dismiss()
            }
            builder.create()
        }
    }

    /*
    private fun initialize() {
        val view: View = LayoutInflater.from(context).inflate(R.layout.custom_dialog_add_review, null)
        setView(view)
        val builder = Builder(context)
        builder.setView(view)
        val rating = view.findViewById<RatingBar>(R.id.rating)
        val textReview = view.findViewById<EditText>(R.id.et_review)
        val submit = view.findViewById<Button>(R.id.submit_button)
        val cancel = view.findViewById<Button>(R.id.cancel_button)

        submit.setOnClickListener {
            println("${rating.rating}")
            println("$textReview")
            if (textReview.length() < 10)
                textReview.error = "Review size should be at least 10 characters long"
            else{
                println("good review")
                vm.addReview(idReservation, idUser, rating, textReview)

            }
        }

        cancel.setOnClickListener {
            this.dismiss()
        }
    }

     */
}