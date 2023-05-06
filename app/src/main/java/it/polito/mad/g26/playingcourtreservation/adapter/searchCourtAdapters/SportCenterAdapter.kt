package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterServicesCourts

class SportCenterAdapter(private var collection: List<SportCenterServicesCourts>) :
    RecyclerView.Adapter<SportCenterAdapter.SportCenterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCenterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sport_center_item, parent, false)
        return SportCenterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportCenterViewHolder, position: Int) {
        holder.bind(collection[position].sportCenter)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<SportCenterServicesCourts>) {
        this.collection = updatedCollection
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

        //private val rvServices = itemView.findViewById<RecyclerView>(R.id.structureServicesRV)

        //private val rvCourtChild = itemView.findViewById<RecyclerView>(R.id.courtsDataRV)

        fun bind(collection: SportCenter) {
            sportCenterName.text = collection.name
            sportCenterAddress.text = collection.address
            sportCenterHours.text = "${collection.openTime} - ${collection.closeTime}"
            // val courtAdapter = CourtAdapter(collection.courtModels)
            //          rvCourtChild.adapter = courtAdapter

            //      rvServices.adapter = ServicesAdapter(collection.services,{ println(it) },{println(it)})


        }

        fun unbind() {
            sportCenterName.text = ""
            //rvCourtChild.adapter=null
        }

    }


}