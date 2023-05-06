package it.polito.mad.g26.playingcourtreservation.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity


class ReservationXFragment : Fragment(R.layout.fragment_reservation_x) {



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var serviceUsed=listOf("Bathroom", "Var Usage","First Aid Kit","Supporters","MiniBar", "CheatMode")

        //Menu customization
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.show_profile_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Edit
                    R.id.edit_menu_item -> {
                        findNavController().navigate(R.id.action_reservationXFragment_to_modifyReservationXFragment)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // Change Title
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Reservation"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        //Create popup window
        val popupView = layoutInflater.inflate(R.layout.popup_window, null)
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.GREEN))


        //Button for popup
        val deleteButton=view.findViewById<Button>(R.id.delete_reservation)
        deleteButton.setOnClickListener{
            popupWindow.showAtLocation(view, Gravity.CENTER,0,0)
        }

        //Button of the popup
        val deletePopButton=popupView.findViewById<MaterialButton>(R.id.deletePopButton)
        deletePopButton.setOnClickListener{
            popupWindow.dismiss()
            //call the other fragment
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
