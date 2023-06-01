package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R

class ServiceAdapter(
    private var collection: List<String>,
    private val addServiceIdToFilters: (String) -> Unit,
    private val removeServiceIdFromFilters: (String) -> Unit,
    private val isServiceIdInList: (String) -> Boolean,
) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chip_service_item, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(
            collection[position]
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<String>) {
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

        fun bind(serviceName: String) {
            chip.text = serviceName
            chip.isChecked = isServiceIdInList(serviceName)
            chip.isCloseIconVisible = chip.isChecked

            chip.setOnClickListener {
                chip.isCloseIconVisible = chip.isChecked
                if (chip.isChecked)
                    addServiceIdToFilters(serviceName)
                else
                    removeServiceIdFromFilters(serviceName)
            }
        }

        fun unbind() {
            chip.text = ""
            chip.setOnClickListener(null)
        }
    }
}