package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReviewsAdapter
import it.polito.mad.g26.playingcourtreservation.model.avg
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerMCVAnimation
import it.polito.mad.g26.playingcourtreservation.util.startShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerMCVAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.CourtReviewsViewModel

@AndroidEntryPoint
class CourtReviewsFragment : Fragment(R.layout.court_reviews_fragment) {

    private val args: CourtReviewsFragmentArgs by navArgs()
    private val viewModel by viewModels<CourtReviewsViewModel>()

    private lateinit var reviewsRV: RecyclerView
    private lateinit var sportCenterTV: TextView
    private lateinit var sportCenterAddressTV: TextView
    private lateinit var courtTV: TextView
    private lateinit var noReviewMCV: MaterialCardView
    private lateinit var meanRatingMCV: MaterialCardView
    private lateinit var meanRatingBar: RatingBar
    private lateinit var meanRatingTV: TextView
    private lateinit var meanRatingValueTV: TextView
    private lateinit var customToolBar: Toolbar
    private lateinit var reviewsShimmerView: ShimmerFrameLayout
    private lateinit var reviewsMeanShimmerView: ShimmerFrameLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sportCenterId = args.sportCenterId
        val courtId = args.courtId

        /* CUSTOM TOOLBAR MANAGEMENT*/
        customToolBar = view.findViewById(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        // Visual components
        sportCenterTV = view.findViewById(R.id.sportCenterTV)
        sportCenterAddressTV = view.findViewById(R.id.sportCenterAddressTV)
        courtTV = view.findViewById(R.id.courtTV)
        meanRatingMCV = view.findViewById(R.id.meanRatingMCV)
        meanRatingBar = view.findViewById(R.id.meanRating)
        meanRatingValueTV = view.findViewById(R.id.meanRatingValueTV)
        meanRatingTV = view.findViewById(R.id.meanRatingTV)
        reviewsRV = view.findViewById(R.id.reviewsRV)
        noReviewMCV = view.findViewById(R.id.noReviewFoundMCV)

        /* shimmerFrameLayout INITIALIZER */
        reviewsShimmerView = view.findViewById(R.id.reviewsShimmerView)
        reviewsMeanShimmerView = view.findViewById(R.id.reviewsMeanShimmerView)

        // Setup RV
        val reviewsAdapter = ReviewsAdapter(viewModel.courtReviews, viewModel.userInformationMap)
        reviewsRV.adapter = reviewsAdapter

        // Load the data needed
        viewModel.loadCourtReviews(sportCenterId, courtId)
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    reviewsMeanShimmerView.startShimmerMCVAnimation(meanRatingMCV)
                    reviewsShimmerView.startShimmerRVAnimation(reviewsRV)
                }

                is UiState.Failure -> {
                    reviewsMeanShimmerView.stopShimmerMCVAnimation(meanRatingMCV)
                    reviewsShimmerView.stopShimmerRVAnimation(reviewsRV)
                    toast(state.error ?: "Unable to get court reviews")
                }

                is UiState.Success -> {
                    reviewsMeanShimmerView.stopShimmerMCVAnimation(meanRatingMCV)
                    reviewsShimmerView.stopShimmerRVAnimation(reviewsRV)
                    val sportCenter = viewModel.sportCenter
                    val court = viewModel.court
                    val courtReviews = viewModel.courtReviews
                    val userInformationMap = viewModel.userInformationMap
                    // Update Sport Center Information
                    sportCenterTV.text = sportCenter.name
                    sportCenterAddressTV.text = getString(
                        R.string.sport_center_address_res,
                        sportCenter.address, sportCenter.city
                    )
                    // Update Court Information
                    courtTV.text = court.name
                    // Update Reviews Information
                    if (courtReviews.isNotEmpty()) {
                        reviewsRV.makeVisible()
                        noReviewMCV.makeInvisible()
                        meanRatingMCV.makeVisible()
                        meanRatingTV.text =
                            getString(R.string.mean_rating_text, courtReviews.size.toString(),
                                if (courtReviews.size != 1) "s" else ""
                            )
                        val meanRating = courtReviews.avg()
                        meanRatingBar.rating = meanRating
                        meanRatingValueTV.text =
                            getString(R.string.mean_rating_value, String.format("%.2f", meanRating))
                    } else {
                        reviewsRV.makeInvisible()
                        noReviewMCV.makeVisible()
                        meanRatingMCV.makeInvisible()
                    }
                    reviewsAdapter.updateReviews(courtReviews, userInformationMap)
                }
            }
        }

        // Handle Menu Items
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_courts, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}