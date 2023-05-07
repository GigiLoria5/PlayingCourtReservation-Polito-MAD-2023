package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.custom.CourtWithDetails
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.enums.CourtStatus
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithDataFormatted

class SportCenterAdapter(
    private var collection: List<SportCenterWithDataFormatted>,
    private val isCourtReserved: (Int) -> CourtStatus,
    private var iHaveAReservation: Boolean // TODO SE VA BENE IL POPUP, QUESTO PARAMETRO NON SERVE PIù
) :
    RecyclerView.Adapter<SportCenterAdapter.SportCenterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCenterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sport_center_item, parent, false)
        return SportCenterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportCenterViewHolder, position: Int) {
        val sportCenter = collection[position].sportCenter
        val courtsWithDetails = collection[position].courtsWithDetails
        val servicesWithFee = collection[position].servicesWithFee
        holder.bind(
            sportCenter,
            servicesWithFee,
            courtsWithDetails
        ) //TODO X I SERVIZI SELEZIONATI, INVENTATI QUALCOSA

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<SportCenterWithDataFormatted>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reservationsUpdate(doIHaveAReservationOrNot: Boolean) {
        iHaveAReservation = doIHaveAReservationOrNot // TODO SE VA BENE IL POPUP, QUESTO PARAMETRO NON SERVE PIù

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
        private val sportCenterChooseServiceInfo =
            itemView.findViewById<TextView>(R.id.availableServicesTV)

        private val rvServices = itemView.findViewById<RecyclerView>(R.id.sportCenterServicesRV)
        private val rvCourt = itemView.findViewById<RecyclerView>(R.id.courtsDataRV)

        fun bind(
            sportCenter: SportCenter,
            servicesWithFee: List<ServiceWithFee>,
            courts: List<CourtWithDetails>
        ) {
            sportCenterName.text = sportCenter.name
            sportCenterAddress.text = sportCenter.address
            sportCenterHours.text = itemView.context.getString(
                R.string.sport_center_hours,
                sportCenter.openTime,
                sportCenter.closeTime
            )
            if (servicesWithFee.isEmpty()) sportCenterChooseServiceInfo.text =
                itemView.context.getString(R.string.no_services_available)
            rvCourt.adapter = CourtAdapter(courts, isCourtReserved, iHaveAReservation)


            if (iHaveAReservation) {
                sportCenterChooseServiceInfo.visibility=View.GONE
            }else{
                sportCenterChooseServiceInfo.visibility=View.VISIBLE
                //TODO CAPIRE COME GESTIRE I SERVIZI SCELTI
                rvServices.adapter = ServiceAdapter(servicesWithFee.map { it.service },
                    { println(it) },
                    { println(it) },
                    { false })

            }
        }

        fun unbind() {
            sportCenterName.text = ""
            rvServices.adapter = null
            rvCourt.adapter = null
        }
    }
}