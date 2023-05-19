package it.polito.mad.g26.playingcourtreservation.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.viewmodel.CustomDialogAlertAddReviewVM

class CustomDialogAlertAddReview: DialogFragment() {
    companion object {

        const val TAG = "Review Dialog"
        fun newInstance(
            idReservation: Int,
            idUser: Int
        ): CustomDialogAlertAddReview = CustomDialogAlertAddReview().apply {
            arguments = Bundle().apply {
                putInt("idReservation", idReservation)
                putInt("idUser", idUser)
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context.let {

            val idReservation = arguments?.getInt("idReservation")
            val idUser = arguments?.getInt("idUser")
            val view: View = layoutInflater.inflate(R.layout.custom_dialog_add_review, null)
            val builder = AlertDialog.Builder(it!!)
            builder.setView(view)
            val rating = view.findViewById<RatingBar>(R.id.rating)
            val textReview = view.findViewById<EditText>(R.id.et_review)
            val ratingError = view.findViewById<MaterialCardView>(R.id.errorRatingMCV)
            ratingError.makeInvisible()

            val submit = view.findViewById<Button>(R.id.submit_button)
            val cancel = view.findViewById<Button>(R.id.cancel_button)

            val review by viewModels<CustomDialogAlertAddReviewVM>()

            val builderConfirm = AlertDialog.Builder(it)
            builderConfirm .setMessage("Review added successfully")
                .setPositiveButton("Ok"){ _, _ -> }

            val confirmDialog = builderConfirm.create()

            rating.setOnRatingBarChangeListener { _, _, _ ->  ratingError.makeInvisible()}

            submit.setOnClickListener {
                var error = false
                if (textReview.length() < 10)
                {
                    textReview.error = "Review size should be at least 10 characters long"
                    error = true
                }
                if (rating.rating == 0.0f)
                {
                    ratingError.makeVisible()
                    error = true
                }
                if (!error){
                    review.addReview(idReservation!!, idUser!!, rating.rating, textReview.text.toString() )
                    this.dismiss()
                    confirmDialog.show()
                }
            }

            cancel.setOnClickListener {
                this.dismiss()
            }
            builder.create()
        }
    }
}