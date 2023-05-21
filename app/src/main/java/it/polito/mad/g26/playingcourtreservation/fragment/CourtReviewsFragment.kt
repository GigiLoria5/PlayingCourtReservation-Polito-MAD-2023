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
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.ReviewsAdapter
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.viewmodel.CourtReviewsVM
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.util.showActionBar

class CourtReviewsFragment : Fragment(R.layout.fragment_court_reviews) {

    private val args: CourtReviewsFragmentArgs by navArgs()
    private val vm by viewModels<CourtReviewsVM>()

    private lateinit var reviewsRV: RecyclerView
    private lateinit var sportCenterTV: TextView
    private lateinit var courtTV: TextView
    private lateinit var noReviewMCV: MaterialCardView
    private lateinit var meanRatingMCV: MaterialCardView
    private lateinit var meanRatingBar: RatingBar
    private lateinit var meanRatingTV: TextView
    private lateinit var meanRatingValueTV: TextView
    private lateinit var customToolBar: Toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        //Set up SportCenter name and field name
        sportCenterTV = view.findViewById(R.id.sportCenterTV)
        courtTV = view.findViewById(R.id.courtTV)
        vm.getCourt(courtId).observe(viewLifecycleOwner){
            courtTV.text = it.name
            vm.getSportCenterName(it.idSportCenter).observe(viewLifecycleOwner){ scName->
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
                noReviewMCV.makeInvisible()
                meanRatingMCV.makeVisible()

                vm.courtReviewsMean(courtId).observe(viewLifecycleOwner){mean ->
                    meanRatingBar.rating = mean
                    meanRatingValueTV.text = getString(R.string.mean_rating_value, String.format("%.2f", mean))
                }
                vm.courtReviewsCount(courtId).observe(viewLifecycleOwner){count ->
                    meanRatingTV.text = getString(R.string.mean_rating_text, count.toString())
                }

            } else {
                reviewsRV.makeInvisible()
                noReviewMCV.makeVisible()
                meanRatingMCV.makeInvisible()
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
    override fun onResume() {
        super.onResume()
        showActionBar(activity)
    }
}