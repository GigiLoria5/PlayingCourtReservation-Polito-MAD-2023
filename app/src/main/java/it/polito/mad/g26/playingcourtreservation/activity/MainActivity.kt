package it.polito.mad.g26.playingcourtreservation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.*
import it.polito.mad.g26.playingcourtreservation.viewmodel.*

class MainActivity : AppCompatActivity() {
    private val courtVM by viewModels<CourtVM>()
    private val reservationVM by viewModels<ReservationVM>()
    private val reservationServiceVM by viewModels<ReservationServiceVM>()
    private val serviceVM by viewModels<ServiceVM>()
    private val sportCenterVM by viewModels<SportCenterVM>()
    private val sportCenterServicesVM by viewModels<SportCenterServicesVM>()
    private val sportVM by viewModels<SportVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val courtObserver = Observer<List<Court>> { c ->
            println("Court: $c")
        }
        courtVM.courtList.observe(this, courtObserver )

        val reservationObserver = Observer<List<Reservation>> { r ->
            println("Reservations: $r")
        }
        reservationVM.reservationList.observe(this, reservationObserver )

        val reservationServicesObserver = Observer<List<ReservationServices>> { rs ->
            println("Reservation Sevices: $rs")
        }
        reservationServiceVM.reservationServicesList.observe(this, reservationServicesObserver )

        val servicesObserver = Observer<List<Service>> { s ->
            println("Service: $s")
        }
        serviceVM.serviceList.observe(this, servicesObserver )

        val sportObserver = Observer<List<Sport>> { s ->
            println("Sport: $s")
        }
        sportVM.sportsList.observe(this, sportObserver )

        val sportCenterObserver = Observer<List<SportCenter>> { sc ->
            println("Sport Center: $sc")
        }
        sportCenterVM.sportCenterList.observe(this, sportCenterObserver )

        val sportCenterServicesObserver = Observer<List<SportCenterServices>> { scs ->
            println("Sport Center Services: $scs")
        }
        sportCenterServicesVM.sportCenterServicesList.observe(this, sportCenterServicesObserver )

    }
}