package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.HomePageViewModel
import org.json.JSONObject

@AndroidEntryPoint
class HomePageFragment : Fragment(R.layout.home_page_fragment) {

    private val viewModel by viewModels<HomePageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActionBar(activity)

        println("HomePageFragment, at start the current user is: ${viewModel.currentUser?.uid ?: "null"}")

        val cityNameTV = view.findViewById<TextView>(R.id.cityNameTV)
        val selectCityMCV = view.findViewById<MaterialCardView>(R.id.citySearchMCV)
        val searchMCV = view.findViewById<MaterialCardView>(R.id.searchMCV)

        cityNameTV.text = getUserCity() ?: getString(R.string.default_city)
        selectCityMCV.setOnClickListener {
            val direction =
                HomePageFragmentDirections.actionHomeToSportCentersAction(
                    "home", cityNameTV.text.toString(), 0, 0, intArrayOf()
                )
            findNavController().navigate(direction)
        }
        searchMCV.setOnClickListener {
            val direction =
                HomePageFragmentDirections.actionHomeToSearchSportCenters(
                    "home", cityNameTV.text.toString(), 0, 0, intArrayOf()
                )
            findNavController().navigate(direction)
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.currentUser != null)
            return
        viewModel.login()
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Activate Loading Effect
                    println("HomePage Login Loading")
                }

                is UiState.Failure -> {
                    println("HomePage Login Error: ${state.error}")
                }

                is UiState.Success -> {
                    println("HomePage Login Success")
                }

                else -> println("HomePage Login Strange Error")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
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
