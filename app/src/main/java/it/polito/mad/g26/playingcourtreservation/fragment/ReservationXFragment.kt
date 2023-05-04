package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import it.polito.mad.g26.playingcourtreservation.R



class ReservationXFragment : Fragment(R.layout.fragment_reservation_x){

    private lateinit var spinnerSer: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set spinner and adapter
        spinnerSer = view.findViewById(R.id.spinner)
        val adapterSer= ArrayAdapter.createFromResource(requireContext(), R.array.services_array , android.R.layout.simple_spinner_item)
        spinnerSer.adapter=adapterSer

        spinnerSer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinnerSer.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }

        }

    }

}