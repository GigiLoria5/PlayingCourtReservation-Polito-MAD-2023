package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.mad.g26.playingcourtreservation.R


class ReservationXFragment : Fragment(R.layout.fragment_reservation_x) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var serviceUsed=listOf("Bathroom", "Var Usage","First Aid Kit","Supporters","MiniBar", "CheatMode")
        //getResources().getStringArray(R.array.services_array)


        //Button for popup
        val servicesButton=view.findViewById<Button>(R.id.delete_reservation)
        servicesButton.setOnClickListener{
            //move to another fragment
        }


        //recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.service_list)
        recyclerView.adapter=MyAdapterRecycle1(serviceUsed)
        recyclerView.layoutManager= GridLayoutManager(context,3)






    }


}

//Class to contain the view of a single random item
class MyViewHolder1 (v:View) : RecyclerView.ViewHolder(v){

    private val cBox=v.findViewById<MaterialButton>(R.id.material_button)

    fun bind(s:String){
        cBox.text=s
    }
}


class MyAdapterRecycle1( val l :List<String>) : RecyclerView.Adapter<MyViewHolder1>(){

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder1 {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.service_recycler,parent,false)
        return MyViewHolder1(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: MyViewHolder1, position: Int) {
        holder.bind(l[position])
    }
}
