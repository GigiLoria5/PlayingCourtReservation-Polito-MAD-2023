package it.polito.mad.g26.playingcourtreservation.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.g26.playingcourtreservation.database.CourtReservationDatabase
import it.polito.mad.g26.playingcourtreservation.model.*

class ReviewRepository(application: Application) {
    private val reviewDao = CourtReservationDatabase.getDatabase(application).reviewDao()

    fun reviews(): LiveData<List<Review>> = reviewDao.findAll()
    fun addReview(idReservation: Int, idUser: Int, rating: Float, textReview: String, date:String){
        val review = Review().also {
            it.idReservation = idReservation
            it.idUser = idUser
            it.rating = rating
            it.textReview = textReview
            it.date = date
        }
        return reviewDao.addReview(review)
    }
}