package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithServicesFormatted

class SportCenterAdapter(
    private var collection: List<SportCenterWithServicesFormatted>,
    private val isServiceIdInList: (Int) -> Boolean
) :
    RecyclerView.Adapter<SportCenterAdapter.SportCenterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCenterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sport_center_item, parent, false)
        return SportCenterViewHolder(view)
    }
    override fun onBindViewHolder(holder: SportCenterViewHolder, position: Int) {
        val sportCenter = collection[position].sportCenter
        val servicesWithFee = collection[position].servicesWithFee
        holder.bind(
            sportCenter,
            servicesWithFee,
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<SportCenterWithServicesFormatted>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }
    override fun onViewRecycled(holder: SportCenterViewHolder) {
        holder.unbind()
    }
    override fun getItemCount() = collection.size
    inner class SportCenterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sportCenterNameTV = itemView.findViewById<TextView>(R.id.sportCenterNameTV)
        private val sportCenterAddressTV = itemView.findViewById<TextView>(R.id.sportCenterAddressTV)
        private val sportCenterHoursTV = itemView.findViewById<TextView>(R.id.sportCenterHoursTV)
        private val availableServicesTV =
            itemView.findViewById<TextView>(R.id.availableServicesTV)
        private val sportCenterServicesRV = itemView.findViewById<RecyclerView>(R.id.sportCenterServicesRV)

        fun bind(
            sportCenter: SportCenter,
            servicesWithFee: List<ServiceWithFee>,
        ) {
            sportCenterNameTV.text = sportCenter.name
            sportCenterAddressTV.text = sportCenter.address
            sportCenterHoursTV.text = itemView.context.getString(
                R.string.sport_center_hours,
                sportCenter.openTime,
                sportCenter.closeTime
            )
            if (servicesWithFee.isEmpty()) availableServicesTV.text =
                itemView.context.getString(R.string.no_services_available)

            sportCenterServicesRV.adapter = ServiceWithFeeAdapter(
                servicesWithFee,
                isServiceIdInList={
                    isServiceIdInList(it)
                },
                isClickable=false
            )
        }

        fun unbind() {
            sportCenterNameTV.text = ""
            sportCenterServicesRV.adapter = null
        }
    }
}