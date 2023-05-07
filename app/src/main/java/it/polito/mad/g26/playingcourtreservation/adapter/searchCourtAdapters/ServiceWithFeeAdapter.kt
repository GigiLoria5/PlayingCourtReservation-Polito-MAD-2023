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
    private val addServiceIdToSelectionList: (Int) -> Unit,
    private val removeServiceIdFromSelectionList: (Int) -> Unit,
    private val isServiceIdInSelectionList: (Int) -> Boolean
) :
    RecyclerView.Adapter<ServiceWithFeeAdapter.ServiceWithFeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceWithFeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_item, parent, false)

        return ServiceWithFeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceWithFeeViewHolder, position: Int) {


        holder.bind(
            collection[position],
            addServiceIdToSelectionList,
            removeServiceIdFromSelectionList,
            isServiceIdInSelectionList
        )
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
            addServiceIdToSelectionList: (Int) -> Unit,
            removeServiceIdFromSelectionList: (Int) -> Unit,
            isServiceIdInSelectionList: (Int) -> Boolean

        ) {
            val service = collection.service
            val fee = collection.fee
            chip.text = itemView.context.getString(
                R.string.service_with_fee,
                service.name,
                String.format("%.2f", fee)
            )

            chip.isChecked = isServiceIdInSelectionList(service.id)

            chip.isCloseIconVisible = chip.isChecked

            chip.setOnClickListener {
                chip.isCloseIconVisible = chip.isChecked
                if (chip.isChecked)
                    addServiceIdToSelectionList(service.id)
                else
                    removeServiceIdFromSelectionList(service.id)
            }
        }

        fun unbind() {
            chip.text = ""
            chip.setOnClickListener(null)
        }

    }
}