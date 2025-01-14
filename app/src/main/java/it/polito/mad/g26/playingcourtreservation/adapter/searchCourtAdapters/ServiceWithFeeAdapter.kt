package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.Service

class ServiceWithFeeAdapter(
    private var collection: List<Service>,
    private val addServiceNameToList: ((String) -> Unit)? = null,
    private val removeServiceNameFromList: ((String) -> Unit)? = null,
    private val isServiceNameInList: (String) -> Boolean,
    private val isClickable: Boolean
) :
    RecyclerView.Adapter<ServiceWithFeeAdapter.ServiceWithFeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceWithFeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chip_service_item, parent, false)
        return ServiceWithFeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceWithFeeViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    override fun onViewRecycled(holder: ServiceWithFeeViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class ServiceWithFeeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val chip = itemView.findViewById<Chip>(R.id.chip)

        fun bind(
            service: Service
        ) {
            val fee = service.fee
            chip.text = itemView.context.getString(
                R.string.service_with_fee,
                service.name,
                String.format("%.2f", fee)
            )

            chip.isChecked = isServiceNameInList(service.name)
            chip.isClickable = isClickable
            chip.isCloseIconVisible = chip.isChecked && isClickable
            when (isClickable) {
                true -> chip.setOnClickListener {
                    chip.isCloseIconVisible = chip.isChecked
                    if (chip.isChecked)
                        addServiceNameToList?.let { it(service.name) }
                    else
                        removeServiceNameFromList?.let { it(service.name) }
                }

                false -> {
                    chip.elevation = 0F
                    if (!chip.isChecked) {
                        chip.setTextColor(
                            ContextCompat.getColorStateList(
                                itemView.context,
                                R.color.grey
                            )
                        )
                        chip.chipStrokeColor = ContextCompat.getColorStateList(
                            itemView.context,
                            R.color.grey
                        )
                        chip.chipStrokeWidth = 0.7f
                    }
                }
            }
        }

        fun unbind() {
            chip.text = ""
            chip.setOnClickListener(null)
        }
    }
}