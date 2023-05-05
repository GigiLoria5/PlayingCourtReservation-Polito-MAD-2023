package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R



class ModifyReservationXFragment : Fragment(R.layout.fragment_modify_reservation_x) {

    private lateinit var spinnerSer: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var time= listOf<String>("14-16","16-18","18-20","20-22")
        var services= listOf("First Aid","Var Technology","Bathroom","MiniBar")

        //Set spinner and adapter
        spinnerSer = view.findViewById(R.id.spinner)
        val adapterSer= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, time)
        spinnerSer.adapter=adapterSer

        spinnerSer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinnerSer.setSelection(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        //recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter=MyAdapterRecycle(services)
        recyclerView.layoutManager=LinearLayoutManager(context)

    }

}

//Class to contain the view of a single random item
class MyViewHolder (v:View) : RecyclerView.ViewHolder(v){

    val rButton=v.findViewById<TextView>(R.id.radioButton)
}


//class that uses the viewHolder to show a specific item
class MyAdapterRecycle( val l :List<String>) : RecyclerView.Adapter<MyViewHolder>(){

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.recycler_services,parent,false)
        return MyViewHolder(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.rButton.text=l[position]
    }
}


