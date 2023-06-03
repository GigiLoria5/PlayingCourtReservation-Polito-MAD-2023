package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.UserSkillsAdapter
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.setImageFromByteArray
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.util.showActionBar
import it.polito.mad.g26.playingcourtreservation.util.startShimmerImgAnimation
import it.polito.mad.g26.playingcourtreservation.util.startShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.startShimmerTVListAnimation
import it.polito.mad.g26.playingcourtreservation.util.startShimmerTextAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerImgAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerTVListAnimation
import it.polito.mad.g26.playingcourtreservation.util.stopShimmerTextAnimation
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.SharedProfileViewModel
import it.polito.mad.g26.playingcourtreservation.viewmodel.ShowProfileViewModel

@AndroidEntryPoint
class ShowProfileFragment : Fragment(R.layout.show_profile_fragment) {

    private val args: ShowProfileFragmentArgs by navArgs()
    private val viewModel by viewModels<ShowProfileViewModel>()
    private lateinit var sharedProfileViewModel: SharedProfileViewModel

    private lateinit var avatarImage: ImageView
    private lateinit var username: TextView
    private lateinit var sportRecycleView: RecyclerView
    private lateinit var secondaryInformationTVList: List<TextView>
    private lateinit var avatarShimmer: ShimmerFrameLayout
    private lateinit var usernameShimmer: ShimmerFrameLayout
    private lateinit var secondaryInformationShimmer: ShimmerFrameLayout
    private lateinit var sportRecycleViewShimmer: ShimmerFrameLayout

    private var _isCurrentUserProfile = true
    val isCurrentUserProfile: Boolean
        get() = _isCurrentUserProfile

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedProfileViewModel =
            ViewModelProvider(requireActivity())[SharedProfileViewModel::class.java]
        val userId = args.userId
        _isCurrentUserProfile = userId == null
        // Hide bottom bar and edit button if is not current user profile
        setupActionBar(
            activity,
            "Profile",
            !isCurrentUserProfile
        )

        // Handle top menu actions
        val menuHost: MenuHost = requireActivity()
        handleMenuAction(menuHost, userId)

        // Setup late init variables
        avatarImage = view.findViewById(R.id.avatar)
        avatarShimmer = view.findViewById(R.id.avatarShimmerView)
        usernameShimmer = view.findViewById(R.id.usernameShimmerView)
        username = view.findViewById(R.id.username)
        secondaryInformationShimmer = view.findViewById(R.id.secondaryInformationShimmerView)
        sportRecycleViewShimmer = view.findViewById(R.id.sportsRVShimmerView)
        sportRecycleView = view.findViewById(R.id.sportsRV)

        // Visual components
        val fullName = view.findViewById<CustomTextView>(R.id.full_name)
            .findViewById<TextView>(R.id.value)
        val age = view.findViewById<CustomTextView>(R.id.age)
            .findViewById<TextView>(R.id.value)
        val gender = view.findViewById<CustomTextView>(R.id.gender)
            .findViewById<TextView>(R.id.value)
        val location = view.findViewById<CustomTextView>(R.id.location)
            .findViewById<TextView>(R.id.value)
        val position = view.findViewById<CustomTextView>(R.id.position)
            .findViewById<TextView>(R.id.value)
        secondaryInformationTVList = listOf(fullName, age, gender, location, position)


        // Load user information
        viewModel.loadCurrentUserInformation(userId)
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    startLoadingAnimation()
                }

                is UiState.Failure -> {
                    stopLoadingAnimation()
                    toast(state.error ?: "Unable to get user information")
                }

                is UiState.Success -> {
                    stopLoadingAnimation()
                    val userInfo = viewModel.userInformation
                    val userImageData = viewModel.userImageData
                    if (userImageData != null) {
                        avatarImage.setImageFromByteArray(userImageData)
                    }
                    sharedProfileViewModel.currentUserInfo = userInfo
                    username.text = userInfo.username
                    fullName.text = userInfo.fullname
                    age.text = userInfo.ageOrDefault()
                    gender.text = userInfo.genderOrDefault()
                    location.text = userInfo.locationOrDefault()
                    position.text = userInfo.positionOrDefault()
                    if (userInfo.skills.isEmpty()) {
                        sportRecycleViewShimmer.makeGone()
                        sportRecycleView.makeGone()
                        return@observe
                    }
                    sportRecycleView.adapter = UserSkillsAdapter(
                        userInfo.skills.sortedBy { -it.rating }
                    )
                    sportRecycleView.layoutManager = LinearLayoutManager(context)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showActionBar(activity)
    }

    private fun startLoadingAnimation() {
        avatarShimmer.startShimmerImgAnimation(avatarImage)
        usernameShimmer.startShimmerTextAnimation(username)
        secondaryInformationShimmer.startShimmerTVListAnimation(
            secondaryInformationTVList
        )
        sportRecycleViewShimmer.startShimmerRVAnimation(sportRecycleView)
    }

    private fun stopLoadingAnimation() {
        avatarShimmer.stopShimmerImgAnimation(avatarImage)
        usernameShimmer.stopShimmerTextAnimation(username)
        secondaryInformationShimmer.stopShimmerTVListAnimation(
            secondaryInformationTVList
        )
        sportRecycleViewShimmer.stopShimmerRVAnimation(sportRecycleView)
    }

    private fun handleMenuAction(menuHost: MenuHost, userId: String?) {
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                if (userId == null) // if not is not the current user
                    menuInflater.inflate(R.menu.show_profile_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Edit (only for current user)
                    R.id.edit_menu_item -> {
                        if (viewModel.loadingState.value is UiState.Success)
                            findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                        true
                    }

                    // Back (only available when called from somewhere else)
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
