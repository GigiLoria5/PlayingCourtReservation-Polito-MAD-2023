package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Notification
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.util.startShimmerTextAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerTextAnimation
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.HomePageViewModel
import pl.droidsonroids.gif.GifImageView

@AndroidEntryPoint
class HomePageFragment : Fragment(R.layout.home_page_fragment) {

    private val viewModel by viewModels<HomePageViewModel>()

    private lateinit var loaderImage: GifImageView
    private lateinit var cityNameTV: TextView
    private lateinit var cityShimmerView: ShimmerFrameLayout
    private lateinit var selectCityMCV: MaterialCardView
    private lateinit var searchMCV: MaterialCardView

    private lateinit var allSportName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActionBar(activity)

        // Setup visual components
        loaderImage = requireActivity().findViewById(R.id.loaderImage)
        cityNameTV = view.findViewById(R.id.cityNameTV)
        cityShimmerView = view.findViewById(R.id.cityShimmerView)
        selectCityMCV = view.findViewById(R.id.citySearchMCV)
        searchMCV = view.findViewById(R.id.searchMCV)

        // Handle navigation
        allSportName = requireContext().getString(R.string.all_sports)
        selectCityMCV.setOnClickListener {
            val direction =
                HomePageFragmentDirections.actionHomeToSportCentersAction(
                    "home", cityNameTV.text.toString(), 0, allSportName, arrayOf()
                )
            findNavController().navigate(direction)
        }

        val notificationBell = view.findViewById<ImageView>(R.id.bellIV)
        val noReadNotificationCounterMCV =
            view.findViewById<MaterialCardView>(R.id.notificationCountMCV)
        val noReadNotificationCounterTV = view.findViewById<TextView>(R.id.notificationCountTV)
        // Load the data needed
        if (viewModel.currentUser != null) {
            viewModel.loadNotifications()
            viewModel.loadingState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UiState.Loading -> {
                        loaderImage.setFreezesAnimation(false)
                        loaderImage.makeVisible()
                    }

                    is UiState.Failure -> {
                        toast(state.error ?: "Unable to get notifications")
                    }

                    is UiState.Success -> {
                        loaderImage.makeGone()
                        val notifications = viewModel.notifications
                        if (notifications.isNotEmpty()) {
                            val countNoRead = countNoReadNotifications(notifications)
                            if (countNoRead == 0) {
                                noReadNotificationCounterMCV.makeInvisible()
                            } else {
                                noReadNotificationCounterMCV.makeVisible()
                                noReadNotificationCounterTV.text = countNoRead.toString()
                            }
                        } else {
                            noReadNotificationCounterMCV.makeInvisible()
                        }
                    }
                }
            }
        }

        notificationBell.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.currentUser == null)
            viewModel.login()
        else
            viewModel.getCurrentUserInformation()

        // Handle state changes
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    loaderImage.setFreezesAnimation(false)
                    loaderImage.makeVisible()
                }

                is UiState.Failure -> {
                    loaderImage.setFreezesAnimation(true)
                    showLoginErrorDialog(state.error)
                }

                is UiState.Success -> {
                    loaderImage.makeGone()
                    viewModel.getCurrentUserInformation()
                }
            }
        }

        viewModel.currentUserInformation.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    cityShimmerView.startShimmerTextAnimation(cityNameTV)
                }

                is UiState.Failure -> {
                    cityShimmerView.stopShimmerTextAnimation(cityNameTV)
                    toast(state.error ?: "Unable to load user information")
                }

                is UiState.Success -> {
                    val cityName = state.result.location ?: ""
                    cityNameTV.text = cityName
                    cityShimmerView.stopShimmerTextAnimation(cityNameTV)
                    searchMCV.setOnClickListener {
                        val direction =
                            if (cityName.isNotEmpty())
                                HomePageFragmentDirections.actionHomeToSearchSportCenters(
                                    "home", cityNameTV.text.toString(), 0, allSportName, arrayOf()
                                )
                            else
                                HomePageFragmentDirections.actionHomeToSportCentersAction(
                                    "home", cityNameTV.text.toString(), 0, allSportName, arrayOf()
                                )
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }

    private fun showLoginErrorDialog(error: String?) {
        val defaultMessage = "An error occurred while performing the login"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.dialog_generic_title))
            .setMessage(error ?: defaultMessage)
            .setPositiveButton(resources.getString(R.string.dialog_try_again_title)) { dialog, _ ->
                viewModel.login()
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun countNoReadNotifications(notifications: List<Notification>): Int {
        var count = 0
        for (n in notifications) {
            if (!n.isRead) count++
        }
        return count
    }

}
