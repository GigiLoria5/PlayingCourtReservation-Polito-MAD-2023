package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.newModel.Service

class ChooseAvailableServiceAdapter(
    private var collection: List<Service>,
    private val addServiceToList: ((Service) -> Unit)? = null,
    private val removeServiceFromList: ((Service) -> Unit)? = null,
    private val isServiceInList: (Service) -> Boolean,
) :
    RecyclerView.Adapter<ChooseAvailableServiceAdapter.ChooseAvailableServiceViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChooseAvailableServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.complete_reservation_available_service_item, parent, false)
        return ChooseAvailableServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseAvailableServiceViewHolder, position: Int) {
        holder.bind(collection[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<Service>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }


    override fun onViewRecycled(holder: ChooseAvailableServiceViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class ChooseAvailableServiceViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val availableServiceMCV =
            itemView.findViewById<MaterialCardView>(R.id.availableServiceMCV)
        private val serviceNameTV = itemView.findViewById<TextView>(R.id.serviceNameTV)
        private val servicePriceTV = itemView.findViewById<TextView>(R.id.servicePriceTV)
        private val serviceSelectedIV = itemView.findViewById<ImageView>(R.id.serviceSelectedIV)

        private fun setColors(
            cardColor: Int,
            primaryTextColor: Int,
            secondaryTextColor: Int,
            cardSelected: Boolean
        ) {
            val cardColorContext = ContextCompat.getColor(itemView.context, cardColor)
            val primaryTextColorContext = ContextCompat.getColor(itemView.context, primaryTextColor)
            val secondaryTextColorContext =
                ContextCompat.getColor(itemView.context, secondaryTextColor)

            availableServiceMCV.apply {
                this.setCardBackgroundColor(cardColorContext)
            }
            serviceNameTV.apply {
                this.setTextColor(primaryTextColorContext)
            }
            servicePriceTV.apply {
                this.setTextColor(secondaryTextColorContext)
            }
            serviceSelectedIV.apply {
                visibility = if (cardSelected) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        }

        fun bind(
            service: Service
        ) {
            val serviceName = service.name
            val fee = service.fee
            serviceNameTV.text = serviceName
            servicePriceTV.text =
                itemView.context.getString(
                    R.string.just_total_reservation_price,
                    String.format("%.2f", fee)
                )

            availableServiceMCV.setOnClickListener {
                when (isServiceInList(service)) {
                    true -> {
                        removeServiceFromList?.let { it1 -> it1(service) }
                        setColors(R.color.grey_light_2, R.color.custom_black, R.color.grey, false)
                    }

                    false -> {
                        addServiceToList?.let { it1 -> it1(service) }
                        setColors(R.color.green_500, R.color.white, R.color.white, true)
                    }
                }
            }
        }

        fun unbind() {
            serviceNameTV.text = ""
            servicePriceTV.text = ""
            availableServiceMCV.setOnClickListener(null)
        }
    }
}
