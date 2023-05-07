package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar

class SearchCourtFragment : Fragment(R.layout.fragment_search_court) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Search Court", false)
    }
}