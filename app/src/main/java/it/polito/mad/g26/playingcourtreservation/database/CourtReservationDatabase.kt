package it.polito.mad.g26.playingcourtreservation.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.polito.mad.g26.playingcourtreservation.dao.*
import it.polito.mad.g26.playingcourtreservation.model.*

@Database(entities = [
    Court::class,
    Reservation::class,
    ReservationServices::class,
    Review::class,
    Service::class,
    SportCenter::class,
    SportCenterServices::class,
    Sport::class], version = 1)
abstract class CourtReservationDatabase: RoomDatabase() {
    abstract fun courtDao(): CourtDao
    abstract fun reservationDao(): ReservationDao
    abstract fun reservationWithDetailsDao(): ReservationWithDetailsDao
    abstract fun reservationServiceDao(): ReservationServicesDao
    abstract fun serviceDao(): ServiceDao
    abstract fun sportCenterDao(): SportCenterDao
    abstract fun sportCenterServiceDao(): SportCenterServicesDao
    abstract fun sportDao(): SportDao
    abstract fun reviewDao(): ReviewDao

    companion object{
        @Volatile
        private var INSTANCE: CourtReservationDatabase? = null

        fun getDatabase(context:Context): CourtReservationDatabase =
            (
                    INSTANCE?:
                    synchronized(this){
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