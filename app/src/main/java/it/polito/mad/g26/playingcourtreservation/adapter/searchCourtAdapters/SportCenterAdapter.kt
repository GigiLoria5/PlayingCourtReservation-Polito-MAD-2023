package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.model.SportCenter
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterReviewsSummary
import it.polito.mad.g26.playingcourtreservation.model.custom.SportCenterWithMoreDetailsFormatted
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration

class SportCenterAdapter(
    private var collection: List<SportCenterWithMoreDetailsFormatted>,
    private val isServiceIdInList: (Int) -> Boolean,
    private val navigateToSearchCourtFragment: (Int) -> Unit,
) :
    RecyclerView.Adapter<SportCenterAdapter.SportCenterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCenterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sport_center_item, parent, false)
        return SportCenterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportCenterViewHolder, position: Int) {
        val sportCenter = collection[position].sportCenter
        val reviewsSummary = collection[position].sportCenterReviewsSummary
        val servicesWithFee = collection[position].servicesWithFee
        holder.bind(
            sportCenter,
            reviewsSummary,
            servicesWithFee,
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<SportCenterWithMoreDetailsFormatted>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: SportCenterViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size
    inner class SportCenterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sportCenterInfoMCV =
            itemView.findViewById<MaterialCardView>(R.id.sportCenterInfoMCV)
        private val sportCenterNameTV = itemView.findViewById<TextView>(R.id.sportCenterNameTV)
        private val sportCenterReviewsTV =
            itemView.findViewById<TextView>(R.id.sportCenterReviewsTV)
        private val sportCenterAddressTV =
            itemView.findViewById<TextView>(R.id.sportCenterAddressTV)
        private val sportCenterHoursTV = itemView.findViewById<TextView>(R.id.sportCenterHoursTV)
        private val availableServicesTV =
            itemView.findViewById<TextView>(R.id.availableServicesTV)
        private val sportCenterServicesRV =
            itemView.findViewById<RecyclerView>(R.id.sportCenterServicesRV)

        init {
            val itemDecoration =
                HorizontalSpaceItemDecoration(itemView.context.resources.getDimensionPixelSize(R.dimen.chipDistance))
            sportCenterServicesRV.addItemDecoration(itemDecoration)
        }
        fun bind(
            sportCenter: SportCenter,
            reviewsSummary: SportCenterReviewsSummary,
            servicesWithFee: List<ServiceWithFee>,
        ) {
            sportCenterNameTV.text = sportCenter.name
            sportCenterReviewsTV.text = itemView.context.getString(
                R.string.reviews_summary,
                String.format("%.2f", reviewsSummary.avg),
                reviewsSummary.count.toString()
            )
            sportCenterAddressTV.text = sportCenter.address
            sportCenterHoursTV.text = itemView.context.getString(
                R.string.sport_center_hours,
                sportCenter.openTime,
                sportCenter.closeTime
            )
            if (servicesWithFee.isEmpty()) availableServicesTV.text =
                itemView.context.getString(R.string.no_services_available)

            sportCenterServicesRV.adapter = ServiceWithFeeAdapter(
                servicesWithFee,
                isServiceIdInList = {
                    isServiceIdInList(it)
                },
                isClickable = false
            )
            sportCenterInfoMCV.setOnClickListener {
                navigateToSearchCourtFragment(sportCenter.id)

            }
        }

        fun unbind() {
            sportCenterNameTV.text = ""
            sportCenterServicesRV.adapter = null
        }
    }
}