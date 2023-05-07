package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee

class ServiceWithFeeAdapter(
    private var collection: List<ServiceWithFee>,
    private val addServiceId: (Int) -> Unit,
    private val removeServiceId: (Int) -> Unit,
    private val isServiceIdInList: (Int) -> Boolean
) :
    RecyclerView.Adapter<ServiceWithFeeAdapter.ServiceWithFeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceWithFeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_item, parent, false)

        return ServiceWithFeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceWithFeeViewHolder, position: Int) {


        holder.bind(collection[position], addServiceId, removeServiceId,isServiceIdInList)
    }

    override fun onViewRecycled(holder: ServiceWithFeeViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class ServiceWithFeeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val chip = itemView.findViewById<Chip>(R.id.chip)

        fun bind(
            collection: ServiceWithFee,
            addServiceId: (Int) -> Unit,
            removeServiceId: (Int) -> Unit,
            isServiceIdInList: (Int) -> Boolean
        ) {
            val service=collection.service
            val fee=collection.fee
            chip.text = itemView.context.getString(
                R.string.service_with_fee,
                service.name,
                String.format("%.2f",fee)

            )
            val isInList=isServiceIdInList(service.id)
            chip.isChecked = isInList
            if(isInList)
                addServiceId(service.id)

            chip.isCloseIconVisible = chip.isChecked

            chip.setOnClickListener {
                chip.isCloseIconVisible = chip.isChecked
                if (chip.isChecked)
                    addServiceId(service.id)
                else
                    removeServiceId(service.id)
            }
        }

        fun unbind() {
            chip.text = ""
            chip.setOnClickListener(null)
        }

    }
}