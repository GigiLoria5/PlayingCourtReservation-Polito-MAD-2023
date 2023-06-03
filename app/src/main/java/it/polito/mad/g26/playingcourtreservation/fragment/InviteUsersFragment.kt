package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.InviteUserAdapter
import it.polito.mad.g26.playingcourtreservation.adapter.PositionsAdapter
import it.polito.mad.g26.playingcourtreservation.model.User
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.InviteUsersViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.polito.mad.g26.playingcourtreservation.util.toastShort


@AndroidEntryPoint
class InviteUsersFragment : Fragment(R.layout.invite_users_fragment) {

    private val args: InviteUsersFragmentArgs by navArgs()
    private val viewModel by viewModels<InviteUsersViewModel>()

    /*   VISUAL COMPONENTS       */
    private lateinit var customToolBar: Toolbar
    private lateinit var searchInputET: EditText
    private lateinit var filterByAgeTV: TextView
    private lateinit var ageRS: RangeSlider
    private lateinit var filterBySkillsTV: TextView
    private lateinit var skillRS: RangeSlider
    private lateinit var positionsRV: RecyclerView
    private lateinit var positionsAdapter: PositionsAdapter
    private lateinit var numberOfFoundUsersTV: TextView
    private lateinit var usersRV: RecyclerView
    private lateinit var inviteUsersAdapter: InviteUserAdapter
    private lateinit var usersShimmerView: ShimmerFrameLayout
    private lateinit var noUsersFoundTV: TextView


    /* SUPPORT VARIABLES */
    private var navigatingToOtherFragment = false

    /* ARGS */
    private var city: String = ""
    private var reservationId: String = ""
    private var date: String = ""
    private var time: String = ""
    private var sport: String = ""
    private var positions = setOf<String>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        city = args.city
        reservationId = args.reservationId
        date = args.date
        time = args.time
        sport = args.sport
        positions = resources.getStringArray(R.array.position_array).toSet()

        /* VM INITIALIZATIONS */
        viewModel.initialize(city, reservationId, date, time, sport, positions)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        customToolBar = view.findViewById(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        /*searchInputET INITIALIZER */
        searchInputET = view.findViewById(R.id.searchInputET)
        searchInputET.doOnTextChanged { text, _, _, _ ->
            viewModel.changeFilteredUsername(text.toString())
        }

        /*filterByAgeTV INITIALIZER */
        filterByAgeTV = view.findViewById(R.id.filterByAgeTV)
        filterByAgeTV.text = getString(
            R.string.filter_by_age,
            viewModel.minAge.toInt().toString(),
            viewModel.maxAge.toInt().toString()
        )

        /*ageRS INITIALIZER*/
        ageRS = view.findViewById(R.id.ageRS)
        ageRS.valueFrom = viewModel.minAge
        ageRS.valueTo = viewModel.maxAge
        ageRS.values = listOf(viewModel.minAge, viewModel.maxAge)
        ageRS.addOnChangeListener { _, _, _ ->
            filterByAgeTV.text = getString(
                R.string.filter_by_age,
                ageRS.values[0].toInt().toString(),
                ageRS.values[1].toInt().toString()
            )
            viewModel.changeSelectedMinAge(ageRS.values[0])
            viewModel.changeSelectedMaxAge(ageRS.values[1])
        }

        /*filterBySkillsTV INITIALIZER */
        filterBySkillsTV = view.findViewById(R.id.filterBySkillsTV)
        filterBySkillsTV.text = getString(
            R.string.filter_by_skills,
            String.format("%.1f", viewModel.minSkill),
            String.format("%.1f", viewModel.maxSkill)
        )

        /*skillRS INITIALIZER*/
        skillRS = view.findViewById(R.id.skillRS)
        skillRS.valueFrom = viewModel.minSkill
        skillRS.valueTo = viewModel.maxSkill
        skillRS.values = listOf(viewModel.minSkill, viewModel.maxSkill)
        skillRS.addOnChangeListener { _, _, _ ->
            filterBySkillsTV.text = getString(
                R.string.filter_by_skills,
                String.format("%.1f", skillRS.values[0]),
                String.format("%.1f", skillRS.values[1])
            )
            viewModel.changeSelectedMinSkill(skillRS.values[0])
            viewModel.changeSelectedMaxSkill(skillRS.values[1])
        }

        /* positionsRV RECYCLE VIEW INITIALIZER*/
        positionsRV = view.findViewById(R.id.positionsRV)
        positionsAdapter = createPositionsAdapter()
        positionsRV.adapter = positionsAdapter
        val itemDecoration =
            HorizontalSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.chip_distance))
        positionsRV.addItemDecoration(itemDecoration)

        /* numberOfFoundUsersTV INITIALIZER */
        numberOfFoundUsersTV = view.findViewById(R.id.numberOfFoundUsersTV)


        /* USERS RECYCLE VIEW INITIALIZER*/
        usersRV = view.findViewById(R.id.usersRV)
        inviteUsersAdapter = createInviteUserAdapter()
        usersRV.adapter = inviteUsersAdapter
        noUsersFoundTV = view.findViewById(R.id.noUsersFoundTV)
        handleConfirmInvitation()
        /* shimmerFrameLayout INITIALIZER */
        usersShimmerView = view.findViewById(R.id.usersShimmerView)


    }

    private fun createPositionsAdapter(): PositionsAdapter {
        return PositionsAdapter(
            positions.toList(),
            { viewModel.addPositionToFilters(it) },
            { viewModel.removePositionFromFilters(it) },
            { viewModel.isPositionInList(it) },
            { viewModel.numberOfSelectedPositions() }
        )
    }

    private fun createInviteUserAdapter(): InviteUserAdapter {
        return InviteUserAdapter(
            viewModel.users,
            viewModel.userPicturesMap,
            { viewModel.isUserIdInvited(it) },
            sport,
            { userId ->
                navigatingToOtherFragment = true
                val direction =
                    InviteUsersFragmentDirections.actionInviteUsersFragmentToShowProfileFragment(
                        userId
                    )
                findNavController().navigate(direction)
            },
            { user -> showConfirmationDialog(user)
            }
        )
    }

    private fun showConfirmationDialog(user: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm the invitation")
            .setMessage("Confirm the invitation for ${user.username}?")
            .setPositiveButton("Confirm") { dialog, _ ->
                dialog.dismiss()
                viewModel.inviteAndNotifyUser(user.id)
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun handleConfirmInvitation() {
        viewModel.invitationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    //LOADING IS MANAGED BY _loadingState
                }

                is UiState.Failure -> {
                    toast(state.error ?: "Unable to send the invitation")
                }

                is UiState.Success -> {
                    toastShort("The invitation was successfully sent")
                }
            }
        }
    }

    private fun loadAvailableUsers() {
        /* USERS LOADING */
        viewModel.fetchUsersData()
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    usersShimmerView.startShimmerRVAnimation(
                        usersRV
                    )
                    numberOfFoundUsersTV.makeGone()
                    noUsersFoundTV.makeGone()
                }

                is UiState.Failure -> {
                    usersShimmerView.stopShimmer()
                    usersShimmerView.makeInvisible()
                    toast(state.error ?: "Unable to get users")
                }

                is UiState.Success -> {
                    usersShimmerView.stopShimmer()
                    usersShimmerView.makeInvisible()
                    numberOfFoundUsersTV.makeVisible()
                    val users = viewModel.users
                    val userPictures=viewModel.userPicturesMap
                    val numberOfAvailableUsersFound = users.size
                    numberOfFoundUsersTV.text = getString(
                        R.string.found_users_results_info,
                        numberOfAvailableUsersFound,
                        if (numberOfAvailableUsersFound != 1) "s" else ""
                    )
                    inviteUsersAdapter.updateCollection(users,userPictures)
                    if (numberOfAvailableUsersFound > 0) {
                        usersRV.makeVisible()
                    } else {
                        noUsersFoundTV.makeVisible()
                        usersRV.makeInvisible()
                    }
                }
            }
        }
    }


    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (nextAnim != 0) {
            val anim: Animation = AnimationUtils.loadAnimation(activity, nextAnim)
            anim.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                    if (!navigatingToOtherFragment) {
                        numberOfFoundUsersTV.makeGone()
                        usersRV.makeInvisible()
                        noUsersFoundTV.makeGone()
                    }
                }

                override fun onAnimationRepeat(animation: Animation) {
                    //unuseful
                }

                override fun onAnimationEnd(animation: Animation) {
                    if (enter) {
                        loadAvailableUsers()
                    }
                }
            })
            return anim
        } else
            null
    }

    override fun onResume() {
        super.onResume()
        navigatingToOtherFragment = false
        hideActionBar(activity)
    }

}
