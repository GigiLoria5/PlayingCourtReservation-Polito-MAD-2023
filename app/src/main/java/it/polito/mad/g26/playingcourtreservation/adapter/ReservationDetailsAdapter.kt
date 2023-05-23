package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee

class ReservationDetailsAdapter(private val l: List<ServiceWithFee>, private val empty: Boolean) :
    RecyclerView.Adapter<ReservationDetailsAdapter.ReservationDetailsViewHolder>() {

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationDetailsViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.service_recycler, parent, false)
        return ReservationDetailsViewHolder(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: ReservationDetailsViewHolder, position: Int) {
        holder.bind(l[position], empty)
    }

    inner class ReservationDetailsViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val cBox = v.findViewById<MaterialButton>(R.id.material_button)

        fun bind(s: ServiceWithFee, empty: Boolean) {
            if (empty)
                cBox.text = super.itemView.context.getString(R.string.no_services_chosen)
            else
                cBox.text = super.itemView.context.getString(
                    R.string.reservation_details_concatenation,
                    s.service.name,
                    s.fee.toString()
                )
        }
    }
}