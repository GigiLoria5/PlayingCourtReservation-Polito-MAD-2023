package it.polito.mad.g26.playingcourtreservation.fragment

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity


class ModifyReservationXFragment : Fragment(R.layout.fragment_modify_reservation_x) {

    private lateinit var spinnerSer: Spinner






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var time= mutableListOf("16-18","18-20","20-22")
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



        //val to change
        var services= listOf(Service(1,"First Aid"),Service(2,"Bathroom"),Service(3,"Var Usage"),Service(4,"LockRoom"),Service(5,"MiniBar"))
        var servicesUsed=mutableListOf(Service(1,"First Aid"),Service(2,"Bathroom"))


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
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_chip)
        recyclerView.adapter=MyAdapterRecycle(services, servicesUsed)
        recyclerView.layoutManager=GridLayoutManager(context,3)


    }

}

//classe da eliminare
class Service( var id :Int, var name: String){

    fun addService(s:MutableList<Service>) {
        if(this in s) s.add(this)
    }

    fun removeService(s:MutableList<Service>){
        if(this in s) s.remove(this)
    }

}



//Class to contain the view of a single random item
class MyViewHolder (v:View) : RecyclerView.ViewHolder(v){

    private val chip=v.findViewById<Chip>(R.id.chip)

    fun bind(s: Service, lUsed : MutableList<Service>, l : List<Service> ){
        chip.text=s.name
        chip.isChecked = s in lUsed
        chip.isCloseIconVisible = chip.isChecked
        chip.setOnClickListener {
            chip.isCloseIconVisible = chip.isChecked
            if (chip.isChecked)
                s.addService(lUsed)
            else
                s.removeService(lUsed)
        }
    }


    fun unbind() {
        chip.text = ""
        chip.setOnClickListener(null)
    }
}




//class that uses the viewHolder to show a specific item
class MyAdapterRecycle( val l :List<Service>, var lUsed : MutableList<Service>) : RecyclerView.Adapter<MyViewHolder>(){

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.reservation_chip,parent,false)
        return MyViewHolder(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(l[position],lUsed,l)
    }
}

