package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.adapter.InviteUserAdapter
import it.polito.mad.g26.playingcourtreservation.util.UiState
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.util.makeGone
import it.polito.mad.g26.playingcourtreservation.util.makeInvisible
import it.polito.mad.g26.playingcourtreservation.util.makeVisible
import it.polito.mad.g26.playingcourtreservation.util.startShimmerRVAnimation
import it.polito.mad.g26.playingcourtreservation.util.toast
import it.polito.mad.g26.playingcourtreservation.viewmodel.InviteUsersViewModel


@AndroidEntryPoint
class InviteUsersFragment : Fragment(R.layout.invite_users_fragment) {

    private val args: InviteUsersFragmentArgs by navArgs()
    private val viewModel by viewModels<InviteUsersViewModel>()

    /*   VISUAL COMPONENTS       */
    private lateinit var customToolBar: Toolbar
    private lateinit var numberOfFoundUsersTV: TextView
    private lateinit var usersRV: RecyclerView
    private lateinit var inviteUsersAdapter: InviteUserAdapter
    private lateinit var usersShimmerView: ShimmerFrameLayout

    /* SUPPORT VARIABLES */
    private var navigatingToOtherFragment = false

    /* ARGS */
    private var city: String = ""
    private var reservationId: String = ""
    private var date: String = ""
    private var time: String = ""
    private var sport: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        city = args.city
        reservationId = args.reservationId
        date = args.date
        time = args.time
        sport = args.sport

        println("$city $date $time $reservationId $sport")
        /* VM INITIALIZATIONS */
        viewModel.initialize(city, reservationId, date, time)

        /* CUSTOM TOOLBAR MANAGEMENT*/
        customToolBar = view.findViewById(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        /* INIT VISUAL COMPONENTS */
        numberOfFoundUsersTV = view.findViewById(R.id.numberOfFoundUsersTV)

        /* USERS RECYCLE VIEW INITIALIZER*/
        usersRV = view.findViewById(R.id.usersRV)
        inviteUsersAdapter = createInviteUserAdapter()
        usersRV.adapter = inviteUsersAdapter

        /* shimmerFrameLayout INITIALIZER */
        usersShimmerView = view.findViewById(R.id.usersShimmerView)


    }

    private fun createInviteUserAdapter(): InviteUserAdapter {
        return InviteUserAdapter(
            viewModel.users,
            { viewModel.isUserIdInvited(it) },
            sport,
            { userId ->
                navigatingToOtherFragment = true
                val direction =
                    InviteUsersFragmentDirections.actionInviteUsersFragmentToShowProfileFragment(
                        userId
                    )
                findNavController().navigate(direction)
            }
        )
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
                    //       noAvailableUsersFoundTV.makeGone()
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
                    val numberOfAvailableUsersFound = viewModel.users.size
                    numberOfFoundUsersTV.text = getString(
                        R.string.found_users_results_info,
                        numberOfAvailableUsersFound,
                        if (numberOfAvailableUsersFound != 1) "s" else ""
                    )
                    inviteUsersAdapter.updateCollection(viewModel.users)
                    if (numberOfAvailableUsersFound > 0) {
                        usersRV.makeVisible()
                    } else {
                        //  noAvailableUsersFoundTV.makeVisible()
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
