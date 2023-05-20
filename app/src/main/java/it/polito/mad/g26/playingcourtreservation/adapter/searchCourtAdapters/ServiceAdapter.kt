package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Service

class ServiceAdapter(
    private var collection: List<Service>,
    private val addServiceIdToFilters: (Int) -> Unit,
    private val removeServiceIdFromFilters: (Int) -> Unit,
    private val isServiceIdInList: (Int) -> Boolean,
) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_item, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(
            collection[position]
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<Service>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: ServiceViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class ServiceViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val chip = itemView.findViewById<Chip>(R.id.chip)

        fun bind(
            collection: Service
        ) {
            chip.text = collection.name
            chip.isChecked = isServiceIdInList(collection.id)
            chip.isCloseIconVisible = chip.isChecked

            chip.setOnClickListener {
                chip.isCloseIconVisible = chip.isChecked
                if (chip.isChecked)
                    addServiceIdToFilters(collection.id)
                else
                    removeServiceIdFromFilters(collection.id)
            }
        }

        fun unbind() {
            chip.text = ""
            chip.setOnClickListener(null)
        }
    }
}