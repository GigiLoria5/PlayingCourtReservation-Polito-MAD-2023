package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.HomePageViewModel
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView

@AndroidEntryPoint
class HomePageFragment : Fragment(R.layout.home_page_fragment) {

    private val viewModel by viewModels<HomePageViewModel>()

    private lateinit var loaderImage: GifImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActionBar(activity)

        // Setup late init variables
        loaderImage = requireActivity().findViewById(R.id.loaderImage)

        val cityNameTV = view.findViewById<TextView>(R.id.cityNameTV)
        val selectCityMCV = view.findViewById<MaterialCardView>(R.id.citySearchMCV)
        val searchMCV = view.findViewById<MaterialCardView>(R.id.searchMCV)

        cityNameTV.text = getUserCity() ?: getString(R.string.default_city)
        val allSportName = requireContext().getString(R.string.all_sports)
        selectCityMCV.setOnClickListener {
            val direction =
                HomePageFragmentDirections.actionHomeToSportCentersAction(
                    "home", cityNameTV.text.toString(), 0, allSportName, arrayOf()
                )
            findNavController().navigate(direction)
        }
        searchMCV.setOnClickListener {
            val direction =
                HomePageFragmentDirections.actionHomeToSearchSportCenters(
                    "home", cityNameTV.text.toString(), 0, allSportName, arrayOf()
                )
            findNavController().navigate(direction)
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.currentUser == null)
            viewModel.login()
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    loaderImage.setFreezesAnimation(false)
                    loaderImage.makeVisible()
                }

                is UiState.Success -> {
                    loaderImage.makeGone()
                }

                is UiState.Failure -> {
                    loaderImage.setFreezesAnimation(true)
                    showLoginErrorDialog(state.error)
                }

                else -> {
                    loaderImage.setFreezesAnimation(true)
                    showLoginErrorDialog("")
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

    private fun getUserCity(): String? {
        val sharedPref = this.requireActivity().getSharedPreferences("test", Context.MODE_PRIVATE)
        if (sharedPref.contains("profile")) {
            val json = sharedPref.getString("profile", "Default")?.let { JSONObject(it) }
            if (json != null) {
                return json.getString("location")
            }
            return null
        }
        return null
    }

}
