package it.polito.mad.g26.playingcourtreservation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.polito.mad.g26.playingcourtreservation.dao.CourtDao
import it.polito.mad.g26.playingcourtreservation.dao.ReservationDao
import it.polito.mad.g26.playingcourtreservation.dao.ReservationServicesDao
import it.polito.mad.g26.playingcourtreservation.dao.ReservationWithDetailsDao
import it.polito.mad.g26.playingcourtreservation.dao.ServiceDao
import it.polito.mad.g26.playingcourtreservation.dao.SportCenterDao
import it.polito.mad.g26.playingcourtreservation.dao.SportCenterServicesDao
import it.polito.mad.g26.playingcourtreservation.dao.SportDao
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Reservation
import it.polito.mad.g26.playingcourtreservation.model.ReservationServices
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.Sport
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.SportCenterServices

@Database(
    entities = [
        Court::class,
        Reservation::class,
        ReservationServices::class,
        Service::class,
        SportCenter::class,
        SportCenterServices::class,
        Sport::class], version = 1
)
abstract class CourtReservationDatabase : RoomDatabase() {
    abstract fun courtDao(): CourtDao
    abstract fun reservationDao(): ReservationDao
    abstract fun reservationWithDetailsDao(): ReservationWithDetailsDao
    abstract fun reservationServiceDao(): ReservationServicesDao
    abstract fun serviceDao(): ServiceDao
    abstract fun sportCenterDao(): SportCenterDao
    abstract fun sportCenterServiceDao(): SportCenterServicesDao
    abstract fun sportDao(): SportDao

    companion object {
        @Volatile
        private var INSTANCE: CourtReservationDatabase? = null

        fun getDatabase(context: Context): CourtReservationDatabase =
            (
                    INSTANCE ?: synchronized(this) {
                        val i = INSTANCE ?: Room.databaseBuilder(
                            context.applicationContext,
                            CourtReservationDatabase::class.java,
                            "courtReservation"
                        ).createFromAsset("database/courtReservation.db").build()
                        INSTANCE = i
                        INSTANCE
                    }
                    )!!
    }
}