package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R

class PositionsAdapter(
    private var collection: List<String>,
    private val addPositionToFilters: (String) -> Unit,
    private val removePositionFromFilters: (String) -> Unit,
    private val isPositionInList: (String) -> Boolean,
    private val numberOfSelectedPositions: () -> Int
) :
    RecyclerView.Adapter<PositionsAdapter.PositionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chip_service_item, parent, false)
        return PositionViewHolder(view)
    }

    override fun onBindViewHolder(holder: PositionViewHolder, position: Int) {
        holder.bind(
            collection[position]
        )
    }

    override fun onViewRecycled(holder: PositionViewHolder) {
        holder.unbind()
    }

    override fun getItemCount() = collection.size

    inner class PositionViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val chip = itemView.findViewById<Chip>(R.id.chip)

        fun bind(serviceName: String) {
            chip.text = serviceName
            chip.isChecked = isPositionInList(serviceName)
            chip.isCloseIconVisible = chip.isChecked

            chip.setOnClickListener {
                when (numberOfSelectedPositions() > 1) {
                    true -> {
                        chip.isCloseIconVisible = chip.isChecked
                        if (chip.isChecked)
                            addPositionToFilters(serviceName)
                        else
                            removePositionFromFilters(serviceName)
                    }

                    false -> {
                        if (chip.isChecked) {
                            chip.isCloseIconVisible = true
                            addPositionToFilters(serviceName)

                        } else {
                            chip.isCloseIconVisible = true
                            chip.isChecked = true
                        }
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