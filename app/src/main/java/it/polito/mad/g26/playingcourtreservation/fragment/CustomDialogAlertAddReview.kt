package it.polito.mad.g26.playingcourtreservation.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.viewmodel.CustomDialogAlertAddReviewVM

class CustomDialogAlertAddReview: DialogFragment() {
    private lateinit var rating: RatingBar
    private lateinit var textReview: EditText
    private lateinit var ratingError: MaterialCardView
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
            val view: View = layoutInflater.inflate(R.layout.reservation_details_add_review_dialog, null)
            val builder = AlertDialog.Builder(it!!)
            builder.setView(view)
            rating = view.findViewById(R.id.rating)
            textReview = view.findViewById(R.id.et_review)
            ratingError = view.findViewById(R.id.errorRatingMCV)
            ratingError.makeInvisible()

            val submit = view.findViewById<Button>(R.id.submit_button)
            val cancel = view.findViewById<Button>(R.id.cancel_button)

            val reviewVM by viewModels<CustomDialogAlertAddReviewVM>()

            var update = false

            rating.setOnRatingBarChangeListener { _, _, _ ->  ratingError.makeInvisible()}

            parentFragment?.let { it1 ->
                reviewVM.findReservationReview(idReservation!!, idUser!!).observe(it1.viewLifecycleOwner){ review->
                    if (review != null){
                        update = true
                        rating.rating = review.rating
                        textReview.setText(review.text)
                    }
                }
            }
            submit.setOnClickListener {
                val error = checkReviewValidity()
                if (!error && !update){
                    reviewVM.addReview(idReservation!!, idUser!!, rating.rating, textReview.text.toString().trim() )
                    this.dismiss()
                    Toast.makeText(context, "Review added successfully", Toast.LENGTH_SHORT
                    ).show()
                }
                if (!error && update){
                    reviewVM.updateReview(idReservation!!, idUser!!, rating.rating, textReview.text.toString().trim() )
                    this.dismiss()
                    Toast.makeText(context, "Review added successfully", Toast.LENGTH_SHORT
                    ).show()
                }
            }

            cancel.setOnClickListener {
                this.dismiss()
            }
            builder.create()
        }
    }
    private fun checkReviewValidity(): Boolean{
        var error = false
        if (textReview.text.toString().trim().length < 10)
        {
            textReview.error = "Review size should be at least 10 characters long"
            error = true
        }
        if (rating.rating == 0.0f)
        {
            ratingError.makeVisible()
            error = true
        }
        return error
    }
}