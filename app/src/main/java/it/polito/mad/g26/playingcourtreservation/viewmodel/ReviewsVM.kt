package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.model.Review
import it.polito.mad.g26.playingcourtreservation.repository.ReviewRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class ReviewsVM(application: Application) : AndroidViewModel(application) {

    private val repo = ReviewRepository(application)

    fun addReview(idReservation: Int, idUser: Int, rating: Float, textReview: String){
        thread {
            val date = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            repo.addReview(idReservation, idUser, rating, textReview, date)
        }

    }

    fun courtReviews(courtId: Int): LiveData<List<Review>> = repo.courtReviews(courtId)

}
