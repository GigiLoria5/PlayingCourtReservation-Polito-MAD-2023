package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import it.polito.mad.g26.playingcourtreservation.repository.ReviewRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReviewsVM(application: Application) : AndroidViewModel(application) {

    private val repo = ReviewRepository(application)

    fun addReview(idReservation: Int, idUser: Int, rating: Float, textReview: String){
        val date = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        repo.addReview(idReservation, idUser, rating, textReview, date)
    }

}
