package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee

class ModifyReservationDetailsAdapter(
    private val l: List<ServiceWithFee>,
    private var lUsed: MutableList<ServiceWithFee>,
    private var price: TextView,
    var amount: MutableList<Float>
) : RecyclerView.Adapter<ModifyReservationDetailsAdapter.ModifyReservationDetailsViewHolder>() {
    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModifyReservationDetailsViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.reservation_chip, parent, false)
        return ModifyReservationDetailsViewHolder(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: ModifyReservationDetailsViewHolder, position: Int) {
        holder.bind(l[position], lUsed, price, amount)
    }

    inner class ModifyReservationDetailsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val chip = v.findViewById<Chip>(R.id.chip)
        fun bind(
            s: ServiceWithFee,
            lUsed: MutableList<ServiceWithFee>,
            price: TextView,
            amount: MutableList<Float>
        ) {
            chip.text = super.itemView.context.getString(
                R.string.reservation_info_concatenation,
                s.service.name,
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
                    price.text =super.itemView.context.getString(
                        R.string.set_text_with_euro,
                        amount[0].toString()
                    )
                }
            }
        }
    }
}
