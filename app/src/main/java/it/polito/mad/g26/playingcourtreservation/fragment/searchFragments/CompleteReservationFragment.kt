package it.polito.mad.g26.playingcourtreservation.fragment.searchFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.SearchSportCentersUtil
import it.polito.mad.g26.playingcourtreservation.util.hideActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.CompleteReservationVM

class CompleteReservationFragment : Fragment(R.layout.fragment_complete_reservation) {

    //TODO : THIS VERSION IS JUST TO VERIFY THAT SAFE ARGS WORKS. THIS FRAGMENT, ITS VM AND ITS LAYOUT MUST BE DONE

    private val args: CompleteReservationFragmentArgs by navArgs()
    private val vm by viewModels<CompleteReservationVM>()


    /*   VISUAL COMPONENTS       */
    private lateinit var customToolBar: Toolbar
    private lateinit var sportCenterNameTV: TextView
    private lateinit var sportCenterAddressTV: TextView
    private lateinit var selectedDateTimeTV: TextView
    private lateinit var selectedCourtTV: TextView
    private lateinit var selectedSportTV: TextView
    private lateinit var sportCenterPhoneNumberMCV: MaterialCardView

    /* ARGS */

    private var sportCenterName: String = ""
    private var sportCenterAddress: String = ""
    private var sportCenterPhoneNumber: String = ""
    private var courtId: Int = 0
    private var courtName: String = ""
    private var courtHourCharge: Float = 0f
    private var sportName: String = ""
    private var dateTime: Long = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sportCenterName = args.sportCenterName
        sportCenterAddress = args.sportCenterAddress
        sportCenterPhoneNumber = args.sportCenterPhoneNumber
        courtId = args.courtId
        courtName = args.courtName
        courtHourCharge = args.courtHourCharge
        sportName = args.sportName
        dateTime = args.dateTime
        /* CUSTOM TOOLBAR MANAGEMENT*/
        customToolBar = view.findViewById(R.id.customToolBar)
        customToolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        /*BACK BUTTON MANAGEMENT*/
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        /* INIT SOME VISUAL COMPONENTS */
        sportCenterNameTV = view.findViewById(R.id.sportCenterNameTV)
        sportCenterAddressTV = view.findViewById(R.id.sportCenterAddressTV)
        selectedDateTimeTV = view.findViewById(R.id.selectedDateTimeTV)
        selectedCourtTV = view.findViewById(R.id.selectedCourtTV)
        selectedSportTV = view.findViewById(R.id.selectedSportTV)
        sportCenterPhoneNumberMCV = view.findViewById(R.id.sportCenterPhoneNumberMCV)

        sportCenterNameTV.text = sportCenterName
        sportCenterAddressTV.text = sportCenterAddress
        selectedDateTimeTV.text = getString(
            R.string.selected_date_time_res,
            SearchSportCentersUtil.getDateTimeFormatted(
                dateTime,
                getString(R.string.hourFormat)
            ),
            SearchSportCentersUtil.getDateTimeFormatted(
                dateTime,
                getString(R.string.dateFormat)
            )
        )
        selectedCourtTV.text = courtName
        selectedSportTV.text = sportName

        sportCenterPhoneNumberMCV.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$sportCenterPhoneNumber")
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        hideActionBar(activity)
    }
}