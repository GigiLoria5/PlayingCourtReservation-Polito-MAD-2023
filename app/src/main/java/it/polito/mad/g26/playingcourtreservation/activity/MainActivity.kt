package it.polito.mad.g26.playingcourtreservation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.*

class MainActivity : AppCompatActivity() {
    val vm by viewModels<SimpleVM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val courtObserver = Observer<List<Court>> { c ->
            println("Court: $c")
        }
        vm.courtList.observe(this, courtObserver )

        val reservationObserver = Observer<List<Reservation>> { r ->
            println("Reservations: $r")
        }
        vm.reservationList.observe(this, reservationObserver )

        val reservationServicesObserver = Observer<List<ReservationServices>> { rs ->
            println("Reservation Sevices: $rs")
        }
        vm.reservationServicesList.observe(this, reservationServicesObserver )

        val servicesObserver = Observer<List<Service>> { s ->
            println("Service: $s")
        }
        vm.serviceList.observe(this, servicesObserver )

        val sportObserver = Observer<List<Sport>> { s ->
            println("Sport: $s")
        }
        vm.sportsList.observe(this, sportObserver )

        val sportCenterObserver = Observer<List<SportCenter>> { sc ->
            println("Sport Center: $sc")
        }
        vm.sportCenterList.observe(this, sportCenterObserver )

        val sportCenterServicesObserver = Observer<List<SportCenterServices>> { scs ->
            println("Sport Center Services: $scs")
        }
        vm.sportCenterServiceList.observe(this, sportCenterServicesObserver )

    }
}