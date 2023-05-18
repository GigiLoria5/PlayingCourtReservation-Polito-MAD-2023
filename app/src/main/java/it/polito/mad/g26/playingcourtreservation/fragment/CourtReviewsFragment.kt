package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReviewsAdapter
import it.polito.mad.g26.playingcourtreservation.util.makeInVisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.CourtsVM
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReviewsVM
import it.polito.mad.g26.playingcourtreservation.viewmodel.SportCentersVM

class CourtReviewsFragment : Fragment(R.layout.fragment_court_reviews) {

    private val args: CourtReviewsFragmentArgs by navArgs()
    private val vm by viewModels<ReviewsVM>()
    private val courtVm by viewModels<CourtsVM>()
    private val sportCenterVm by viewModels<SportCentersVM>()

    private lateinit var reviewsRV: RecyclerView
    private lateinit var sportCenterTV: TextView
    private lateinit var courtTV: TextView
    private lateinit var noReviewMCV: MaterialCardView

    private lateinit var meanRatingMCV: MaterialCardView
    private lateinit var meanRatingBar: RatingBar
    private lateinit var meanRatingTV: TextView
    private lateinit var meanRatingValueTV: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Court Reviews", true)

        val courtId = args.courtId

        //Set up SportCenter name and field name
        sportCenterTV = view.findViewById(R.id.sportCenterTV)
        courtTV = view.findViewById(R.id.courtTV)
        courtVm.getCourt(courtId).observe(viewLifecycleOwner){
            courtTV.text = it.name
            sportCenterVm.getSportCenterName(it.idSportCenter).observe(viewLifecycleOwner){ scName->
                sportCenterTV.text = scName
            }
        }

        /*Set-up mean value*/
        meanRatingMCV = view.findViewById(R.id.meanRatingMCV)
        meanRatingBar = view.findViewById(R.id.meanRating)
        meanRatingValueTV = view.findViewById(R.id.meanRatingValueTV)
        meanRatingTV = view.findViewById(R.id.meanRatingTV)

        /*Set-up recycle view */
        reviewsRV = view.findViewById(R.id.reviewsRV)
        val reviewsAdapter = ReviewsAdapter(vm.courtReviews(courtId).value?: listOf())
        noReviewMCV = view.findViewById(R.id.noReviewFoundMCV)

        vm.courtReviews(courtId).observe(viewLifecycleOwner){
            if (it.isNotEmpty()) {
                reviewsRV.makeVisible()
                noReviewMCV.makeInVisible()
                meanRatingMCV.makeVisible()

                vm.courtReviewsMean(courtId).observe(viewLifecycleOwner){mean ->
                    meanRatingBar.rating = mean
                    meanRatingValueTV.text = getString(R.string.mean_rating_value, mean.toString())
                }
                vm.courtReviewsCount(courtId).observe(viewLifecycleOwner){count ->
                    meanRatingTV.text = getString(R.string.mean_rating_text, count.toString())
                }

            } else {
                reviewsRV.makeInVisible()
                noReviewMCV.makeVisible()
                meanRatingMCV.makeInVisible()
            }
            reviewsAdapter.updateReviews(it)
        }

        reviewsRV.adapter = reviewsAdapter

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

}