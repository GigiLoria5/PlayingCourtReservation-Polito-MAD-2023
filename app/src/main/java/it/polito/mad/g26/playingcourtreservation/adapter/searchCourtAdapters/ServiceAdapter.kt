package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Service

class ServiceAdapter(private var collection: List<Service>,
                     private val addService: (Int) -> Unit,
                     private val removeService: (Int) -> Unit) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_item, parent, false)

        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {

        holder.bind(collection[position],addService, removeService)
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
            collection: Service,
            addService: (Int) -> Unit,
            removeService: (Int) -> Unit
        ) {
            chip.text = collection.name
            chip.setOnClickListener {
                chip.isCloseIconVisible = chip.isChecked
                if (chip.isChecked)
                    addService(collection.id)
                else
                    removeService(collection.id)
            }
        }

        fun unbind() {
            chip.text = ""
            chip.setOnClickListener(null)
        }

    }
}