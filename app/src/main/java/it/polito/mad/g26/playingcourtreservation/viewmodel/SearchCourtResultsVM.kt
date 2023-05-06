package it.polito.mad.g26.playingcourtreservation.viewmodel

import android.app.Application
import android.icu.util.Calendar
import androidx.lifecycle.*
import it.polito.mad.g26.playingcourtreservation.util.SearchCourtResultsUtil



class SearchCourtResultsVM(application: Application) : AndroidViewModel(application) {

    private val searchResultUtils = SearchCourtResultsUtil


    /*DATE TIME MANAGEMENT*/
    private val _selectedDateTimeMillis = MutableLiveData<Long>().also {
        it.value = 0
    }
    val selectedDateTimeMillis: LiveData<Long> = _selectedDateTimeMillis
    fun changeSelectedDateTimeMillis(newTimeInMillis: Long) {
        _selectedDateTimeMillis.value = newTimeInMillis
    }

    private fun getDateTimeFormatted(format: String): String {
        val c = Calendar.getInstance()
        c.timeInMillis = selectedDateTimeMillis.value!!
        return searchResultUtils.getDateTimeFormatted(c, format)
    }

}