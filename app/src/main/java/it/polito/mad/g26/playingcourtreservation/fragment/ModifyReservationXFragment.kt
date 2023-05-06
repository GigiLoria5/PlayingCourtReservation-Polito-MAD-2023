package it.polito.mad.g26.playingcourtreservation.fragment

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*


class ModifyReservationXFragment : Fragment(R.layout.fragment_modify_reservation_x) {

    private lateinit var spinnerSer: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var time= mutableListOf("16-18","18-20","20-22")
        var services= listOf("First Aid","Var Technology","Bathroom","MiniBar")
        var servicesUsed=mutableListOf("First Aid","Bathroom")
        var timeSelected="14-16"
        time.add(0,timeSelected)

        //Customization Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.edit_profile_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }
                    // Confirm Changes
                    R.id.confirm_menu_item -> {

                        //Save new profile
                        findNavController().navigate(R.id.reservationXFragment)

                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // Change Title
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Edit Reservation"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)




        //Set spinner and adapter
        spinnerSer = view.findViewById(R.id.spinner)
        val adapterSer= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, time)
        spinnerSer.adapter=adapterSer

        spinnerSer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinnerSer.setSelection(position)
                timeSelected=time[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        //recyclerview
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter=MyAdapterRecycle(services, servicesUsed)
        recyclerView.layoutManager=LinearLayoutManager(context)


    }

}

//Class to contain the view of a single random item
class MyViewHolder (v:View) : RecyclerView.ViewHolder(v){

    private val cBox=v.findViewById<CheckBox>(R.id.checkBox)

    fun bind(s:String, l : MutableList<String> ){
        cBox.text=s
        cBox.isChecked = s in l
        super.itemView.setOnClickListener{
            if(cBox.isChecked) l.add(s)
            else l.remove(s)
        }
    }
}


//class that uses the viewHolder to show a specific item
class MyAdapterRecycle( val l :List<String>, var lUsed : MutableList<String>) : RecyclerView.Adapter<MyViewHolder>(){

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
        holder.bind(l[position],lUsed)
    }
}


