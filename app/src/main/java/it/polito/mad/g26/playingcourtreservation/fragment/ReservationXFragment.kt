package it.polito.mad.g26.playingcourtreservation.fragment



import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM


class ReservationXFragment : Fragment(R.layout.fragment_reservation_x) {

    private val args: ReservationXFragmentArgs by navArgs()
    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()
    private lateinit var serviceUsed : List<Service>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Retrieve Reservation Details
        var reservationId = args.reservationId
        reservationWithDetailsVM
            .getReservationWithDetailsById(reservationId)
            .observe(viewLifecycleOwner) { reservation ->
                println(reservation)
                serviceUsed=reservation.services

                //Recycler view of services
                val recyclerView = view.findViewById<RecyclerView>(R.id.service_list)
                recyclerView.adapter=MyAdapterRecycle1(serviceUsed)
                recyclerView.layoutManager= GridLayoutManager(context,3)
            }



        //Menu customization
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                //Note, same menu of show profile!
                menuInflater.inflate(R.menu.show_profile_menu, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                // Handle the menu selection
                return when (item.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }
                    // Edit
                    R.id.edit_menu_item -> {
                        var action=ReservationXFragmentDirections.openReservationEdit(reservationId)
                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // Change Title
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Reservation"
        // Set Back Button
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)





        //Alert Dialog
        val builder = AlertDialog.Builder(requireContext(),R.style.MyAlertDialogStyle)
        builder.setMessage("Are you sure to delete the reservation?")
        builder.setPositiveButton("OK", { dialog, id ->
            // User clicked OK button
        })
        builder.setNegativeButton("cancel") { _, _ ->
            // User cancelled the dialog
        }

        //Button for show dialog
        val deleteButton=view.findViewById<Button>(R.id.delete_reservation)
        deleteButton.setOnClickListener{
            builder.show()
        }


    }


}

//Class to contain the view of a single random item
class MyViewHolder1 (v:View) : RecyclerView.ViewHolder(v){

    private val cBox=v.findViewById<MaterialButton>(R.id.material_button)

    fun bind(s: Service){
        cBox.text=s.name
    }
}


class MyAdapterRecycle1( val l :List<Service>) : RecyclerView.Adapter<MyViewHolder1>(){

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
