package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.newModel.Review
import it.polito.mad.g26.playingcourtreservation.newModel.Service
import it.polito.mad.g26.playingcourtreservation.newModel.SportCenter
import it.polito.mad.g26.playingcourtreservation.newModel.avg
import it.polito.mad.g26.playingcourtreservation.util.HorizontalSpaceItemDecoration

class SportCenterAdapter(
    private var collection: List<SportCenter>,
    private val reviews: HashMap<String, List<Review>>,
    private val isServiceNameInList: (String) -> Boolean,
    private val navigateToSearchCourtFragment: (String, String, String, String) -> Unit,
) :
    RecyclerView.Adapter<SportCenterAdapter.SportCenterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportCenterViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_sport_centers_sport_center_item, parent, false)
        return SportCenterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportCenterViewHolder, position: Int) {
        val sportCenter = collection[position]
        val sportCenterReviews = reviews[sportCenter.id]!!
        val sportCenterServices = sportCenter.services
        holder.bind(
            sportCenter,
            sportCenterReviews,
            sportCenterServices,
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCollection(updatedCollection: List<SportCenter>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: SportCenterViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size
    inner class SportCenterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sportCenterInfoMCV = itemView
            .findViewById<MaterialCardView>(R.id.sportCenterInfoMCV)
        private val sportCenterNameTV = itemView
            .findViewById<TextView>(R.id.sportCenterNameTV)
        private val sportCenterReviewsTV = itemView
            .findViewById<TextView>(R.id.sportCenterReviewsTV)
        private val sportCenterAddressTV = itemView
            .findViewById<TextView>(R.id.sportCenterAddressTV)
        private val sportCenterHoursTV = itemView
            .findViewById<TextView>(R.id.sportCenterHoursTV)
        private val availableServicesTV = itemView
            .findViewById<TextView>(R.id.availableServicesTV)
        private val sportCenterServicesRV = itemView
            .findViewById<RecyclerView>(R.id.sportCenterServicesRV)

        init {
            val itemDecoration =
                HorizontalSpaceItemDecoration(itemView.context.resources.getDimensionPixelSize(R.dimen.chip_distance))
            sportCenterServicesRV.addItemDecoration(itemDecoration)
        }

        fun bind(
            sportCenter: SportCenter,
            sportCenterReviews: List<Review>,
            sportCenterServices: List<Service>,
        ) {
            sportCenterNameTV.text = sportCenter.name
            sportCenterReviewsTV.text = itemView.context.getString(
                R.string.reviews_summary,
                String.format("%.2f", sportCenterReviews.avg()),
                sportCenterReviews.size.toString()
            )
            sportCenterAddressTV.text = sportCenter.address
            sportCenterHoursTV.text = itemView.context.getString(
                R.string.sport_center_hours,
                sportCenter.openTime,
                sportCenter.closeTime
            )
            if (sportCenterServices.isEmpty()) availableServicesTV.text =
                itemView.context.getString(R.string.no_services_available)

            sportCenterServicesRV.adapter = ServiceWithFeeAdapter(
                sportCenterServices,
                isServiceNameInList = {
                    isServiceNameInList(it)
                },
                isClickable = false
            )
            sportCenterInfoMCV.setOnClickListener {
                val sportCenterAddressFormatted = itemView.context.getString(
                    R.string.sport_center_address_res,
                    sportCenter.address, sportCenter.city
                )
                navigateToSearchCourtFragment(
                    sportCenter.id,
                    sportCenter.name,
                    sportCenterAddressFormatted,
                    sportCenter.phoneNumber
                )

            }
        }

        fun unbind() {
            sportCenterNameTV.text = ""
            sportCenterServicesRV.adapter = null
        }
    }
}