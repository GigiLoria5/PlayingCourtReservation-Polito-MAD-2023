package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Service

class EditReservationDetailsAdapter(
    private val services: List<Service>,
    private var servicesSelected: MutableList<Service>,
    private var price: TextView,
    private var amount: MutableList<Float>
) : RecyclerView.Adapter<EditReservationDetailsAdapter.EditReservationDetailsViewHolder>() {
    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditReservationDetailsViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.chip_service_item, parent, false)
        return EditReservationDetailsViewHolder(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return services.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: EditReservationDetailsViewHolder, position: Int) {
        holder.bind(services[position], servicesSelected, price, amount)
    }

    inner class EditReservationDetailsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val chip = v.findViewById<Chip>(R.id.chip)
        fun bind(
            s: Service,
            lUsed: MutableList<Service>,
            price: TextView,
            amount: MutableList<Float>
        ) {
            chip.text = super.itemView.context.getString(
                R.string.reservation_info_concatenation,
                s.name,
                s.fee.toString()
            )
            chip.isChecked = s in lUsed
            chip.isCloseIconVisible = chip.isChecked
            chip.setOnClickListener {
                chip.isCloseIconVisible = chip.isChecked
                if (chip.isChecked) {
                    lUsed.add(s)
                    amount[0] += s.fee
                    price.text = super.itemView.context.getString(
                        R.string.set_text_with_euro,
                        amount[0].toString()
                    )
                } else {
                    lUsed.remove(s)
                    amount[0] -= s.fee
                    price.text = super.itemView.context.getString(
                        R.string.set_text_with_euro,
                        amount[0].toString()
                    )
                }
            }
        }
    }
}
