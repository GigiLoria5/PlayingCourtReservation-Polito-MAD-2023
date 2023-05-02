package it.polito.mad.g26.playingcourtreservation.adapter.searchCourtAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import it.polito.mad.g26.playingcourtreservation.R

class CityResultAdapter(
    private var collection: List<String>,
    private val goToResult: (String) -> Unit
) :
    RecyclerView.Adapter<CityResultAdapter.CollectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.city_item, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(collection[position], goToResult)
    }

    fun updateCollection(updatedCollection: List<String>) {
        this.collection = updatedCollection
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: CollectionViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityName = itemView.findViewById<TextView>(R.id.cityNameTV)

        private val cityResultMCV = itemView.findViewById<MaterialCardView>(R.id.cityResultMCV)

        fun bind(
            city: String, goToResult: (String) -> Unit
        ) {
            cityName.text = city
            cityResultMCV.setOnClickListener { goToResult(city) }
        }

        fun unbind() {
            cityName.text = ""
            cityResultMCV.setOnClickListener(null)
        }
    }
}