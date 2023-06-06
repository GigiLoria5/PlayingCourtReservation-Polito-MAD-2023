package it.polito.mad.g26.playingcourtreservation.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Debouncer(private val debounceTime: Long) {

    private var debounceJob: Job? = null

    fun submit(action: () -> Unit) {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.Main).launch {
            delay(debounceTime)
            action()
        }
    }

}
