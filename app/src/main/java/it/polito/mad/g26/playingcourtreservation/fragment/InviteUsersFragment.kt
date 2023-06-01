package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar


@AndroidEntryPoint
class InviteUsersFragment : Fragment(R.layout.invite_users_fragment) {

    private val args: InviteUsersFragmentArgs by navArgs()
    //    private val viewModel by viewModels</*TODO*/>()

    /*   VISUAL COMPONENTS       */
    //TODO

    /* ARGS */
    private var city: String = ""
    private var date: String = ""
    private var reservationId: String = ""
    private var time: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        city = args.city
        date = args.date
        reservationId = args.reservationId
        time = args.time

        println(city+" "+date+" "+time+" "+reservationId)
        /* VM INITIALIZATIONS */
        //TODO

        /* CUSTOM TOOLBAR MANAGEMENT*/
        val customToolBar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        customToolBar.title = "Invite users"
        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }

}
