package it.polito.mad.g26.playingcourtreservation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.polito.mad.g26.playingcourtreservation.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemReselectedListener {
            when(it.itemId){
                R.id.home -> supportFragmentManager.beginTransaction().replace(R.id.frame_layout, SearchCourtFragment()).commit()
                //R.id.reservations -> supportFragmentManager.beginTransaction().replace(R.id.frame_layout, Reservation()).commit() DA IMPLEMENTARE
                //R.id.profile -> supportFragmentManager.beginTransaction().replace(R.id.frame_layout, ShowProfileActivity()).commit()
            }
        }
    }

}