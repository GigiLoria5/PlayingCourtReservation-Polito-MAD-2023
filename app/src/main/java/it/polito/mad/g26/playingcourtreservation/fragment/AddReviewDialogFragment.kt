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
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.AddReviewDialogViewModel

@AndroidEntryPoint
class AddReviewDialogFragment : DialogFragment() {

    private val viewModel by viewModels<AddReviewDialogViewModel>()
    private var isUpdate = false
    private var actionLaunched = false

    private lateinit var rating: RatingBar
    private lateinit var textReview: EditText
    private lateinit var ratingError: MaterialCardView

    private lateinit var reservationId: String
    private lateinit var userId: String
    private lateinit var updateParentFragment: () -> Unit

    companion object {
        const val TAG = "Review Dialog"
        fun newInstance(
            reservationId: String,
            userId: String,
            updateParentFragment: () -> Unit
        ): AddReviewDialogFragment {
            return AddReviewDialogFragment().apply {
                this.reservationId = reservationId
                this.userId = userId
                this.updateParentFragment = updateParentFragment
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context.let {
            val view: View = layoutInflater
                .inflate(R.layout.reservation_details_add_review_dialog, null)
            rating = view.findViewById(R.id.rating)
            textReview = view.findViewById(R.id.et_review)
            ratingError = view.findViewById(R.id.errorRatingMCV)
            ratingError.makeInvisible()

            // Alert Builder
            val builder = AlertDialog.Builder(requireContext()).setView(view)
            val submit = view.findViewById<Button>(R.id.submit_button)
            val cancel = view.findViewById<Button>(R.id.cancel_button)
            submit.setOnClickListener {
                handleButtonClick(reservationId, userId)
            }
            cancel.setOnClickListener {
                this.dismiss()
            }
            rating.setOnRatingBarChangeListener { _, _, _ -> ratingError.makeInvisible() }

            // Review Handling
            parentFragment?.let { owner ->
                viewModel.findReservationReview(reservationId, userId)
                viewModel.review.observe(owner) { state ->
                    when (state) {
                        is UiState.Loading -> {
                            // TODO: Show Loading?
                        }

                        is UiState.Failure -> {
                            // TODO: Stop Loading?
                            toast(state.error ?: "Unable to get review")
                        }

                        is UiState.Success -> {
                            // TODO: Stop Loading?
                            val review = state.result
                            isUpdate = review != null
                            rating.rating = review?.rating ?: 0.0F
                            textReview.setText(review?.text ?: "")
                            if (actionLaunched) {
                                val message =
                                    if (isUpdate) "Review updated successfully"
                                    else "Review added successfully"
                                this.dismiss()
                                updateParentFragment()
                                toast(message)
                            }
                        }
                    }
                }
            }

            // Show Dialog
            builder.create()
        }
    }

    private fun handleButtonClick(reservationId: String, userId: String) {
        val error = checkReviewValidity()
        if (!error) {
            val review = Review(
                userId = userId,
                rating = rating.rating,
                text = textReview.text.toString().trim()
            )
            actionLaunched = true
            if (isUpdate) viewModel.updateReview(reservationId, review)
            else viewModel.addReview(reservationId, review)
        }
    }

    private fun checkReviewValidity(): Boolean {
        var error = false
        if (rating.rating == 0.0f) {
            ratingError.makeVisible()
            error = true
        }
        return error
    }

}
