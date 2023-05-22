package it.polito.mad.g26.playingcourtreservation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.mad.g26.playingcourtreservation.R

class EditProfileAdapter ( val list: List<String>, private var rating : MutableList<Float>) :
    RecyclerView.Adapter<EditProfileAdapter.EditProfileViewHolder>() {

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditProfileViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.edit_profile_sport_recycler_view, parent, false)
        return EditProfileViewHolder(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return list.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: EditProfileViewHolder, position: Int) {

            holder.bind(list[position],rating[position],rating,position)
    }

    inner class EditProfileViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        private val text = v.findViewById<TextView>(R.id.sport_title_edit_profile)
        private val starRating= v.findViewById<RatingBar>(R.id.sport_rating_edit_profile)
        private val buttonDelete=v.findViewById<MaterialButton>(R.id.sport_delete_rating)

        fun bind(s: String, r:Float, rList: MutableList<Float>, p:Int) {
            text.text=s
            starRating.rating=r
            starRating.setOnRatingBarChangeListener { _, fl, _ ->
                rList[p]=fl
            }
            buttonDelete.setOnClickListener {
                starRating.rating=0f
                rList[p]=0f
            }
        }
    }
}