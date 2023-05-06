package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Court
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesCourts

class SportCenterAdapter(
    private var collection: List<SportCenterServicesCourts>,
    private var services: List<Service>,
    private val isCourtReserved: (Int) -> Int //0=no, 1=from others, 2=from you

) : //TODO RICEVI ANCHE LO SPORT ID PER FILTRARE I CAMPI
    RecyclerView.Adapter<SportCenterAdapter.SportCenterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCenterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sport_center_item, parent, false)
        return SportCenterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportCenterViewHolder, position: Int) {
        val sportCenter = collection[position].sportCenter
        val courts = collection[position].courts
        val servicesWithFee = collection[position].sportCenterServices.mapNotNull {
            val service = services.find { service -> service.id == it.idService }
            if (service != null)
                ServiceWithFee(service, it.fee)
            else
                null
        }
        holder.bind(sportCenter, servicesWithFee, courts) //TODO X I SERVIZI SELEZIONATI, INVENTATI

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<SportCenterServicesCourts>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAvailableServices(updatedCollection: List<Service>) {
        this.services = updatedCollection
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: SportCenterViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class SportCenterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sportCenterName = itemView.findViewById<TextView>(R.id.sportCenterNameTV)
        private val sportCenterAddress = itemView.findViewById<TextView>(R.id.sportCenterAddressTV)
        private val sportCenterHours = itemView.findViewById<TextView>(R.id.sportCenterHoursTV)

        private val rvServices = itemView.findViewById<RecyclerView>(R.id.sportCenterServicesRV)
        private val rvCourt = itemView.findViewById<RecyclerView>(R.id.courtsDataRV)

        fun bind(
            sportCenter: SportCenter,
            servicesWithFee: List<ServiceWithFee>,
            courts: List<Court>
        ) {
            sportCenterName.text = sportCenter.name
            sportCenterAddress.text = sportCenter.address
            sportCenterHours.text = "${sportCenter.openTime} - ${sportCenter.closeTime}"

            rvCourt.adapter = CourtAdapter(courts, isCourtReserved)


            //TODO CAPIRE COME GESTIRE I SERVIZI SCELTI
            rvServices.adapter = ServiceAdapter(servicesWithFee.map { it.service },
                { println(it) },
                { println(it) },
                { false })


        }

        fun unbind() {
            sportCenterName.text = ""
            rvServices.adapter = null
            //rvCourtChild.adapter=null
        }

    }


}