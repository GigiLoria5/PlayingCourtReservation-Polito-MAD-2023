package it.polito.mad.g26.playingcourtreservation.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import it.polito.mad.g26.playingcourtreservation.R

class ReservationsFragment: Fragment(R.layout.fragment_reservations) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Update Title
        (activity as? AppCompatActivity)?.supportActionBar?.title = "My Reservations"
    }
}