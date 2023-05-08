package it.polito.mad.g26.playingcourtreservation.fragment



import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtResultsVM


class ReservationXFragment : Fragment(R.layout.fragment_reservation_x) {

    private val args: ReservationXFragmentArgs by navArgs()
    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()
    private lateinit var servicesAll : List<ServiceWithFee>
    private lateinit var servicesUsed : List<Service>
    private lateinit var servicesChosen : List<ServiceWithFee>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //List of text
        val center=view.findViewById<CustomTextView>(R.id.center_name)
            .findViewById<TextView>(R.id.value)
        val field=view.findViewById<CustomTextView>(R.id.court_name)
            .findViewById<TextView>(R.id.value)
        val sport=view.findViewById<CustomTextView>(R.id.sport)
            .findViewById<TextView>(R.id.value)
        val city=view.findViewById<CustomTextView>(R.id.city)
            .findViewById<TextView>(R.id.value)
        val address=view.findViewById<CustomTextView>(R.id.address)
            .findViewById<TextView>(R.id.value)
        val price=view.findViewById<CustomTextView>(R.id.price)
            .findViewById<TextView>(R.id.value)
        val date=view.findViewById<CustomTextView>(R.id.date)
            .findViewById<TextView>(R.id.value)
        val time=view.findViewById<CustomTextView>(R.id.time)
            .findViewById<TextView>(R.id.value)

        // Retrieve Reservation Details
        val reservationId = args.reservationId
        reservationWithDetailsVM
            .getReservationWithDetailsById(reservationId)
            .observe(viewLifecycleOwner) { reservation ->
                println("Passed")
                println(reservation)
                servicesUsed=reservation.services
                center.text=reservation.courtWithDetails.sportCenter.name
                field.text=reservation.courtWithDetails.court.name
                sport.text=reservation.courtWithDetails.sport.name
                city.text=reservation.courtWithDetails.sportCenter.city
                address.text=reservation.courtWithDetails.sportCenter.address
                date.text=reservation.reservation.date
                time.text=reservation.reservation.time
                price.text=reservation.reservation.amount.toString()

                reservationWithDetailsVM.getAllServicesWithFee(reservation.courtWithDetails.sportCenter.id)
                    .observe(viewLifecycleOwner){listOfServicesWithSportCenter->
                        //List of services with fee of the sport center
                        val c=listOfServicesWithSportCenter

                        reservationWithDetailsVM.getAllServices().observe(viewLifecycleOwner){listService->
                            //List of all services
                            val d=listService

                            //List of ServiceWithFee of that Sportcenter
                            servicesAll=reservationWithDetailsVM.allServiceWithoutSport(c,d)

                            //Filter service to take only chosen
                            servicesChosen=reservationWithDetailsVM.filterServicesWithFee(servicesAll,servicesUsed)




                            //Recycler view of services
                            val recyclerView = view.findViewById<RecyclerView>(R.id.service_list)
                            recyclerView.adapter=MyAdapterRecycle1(servicesChosen)
                            recyclerView.layoutManager= GridLayoutManager(context,3)
                        }

                    }

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
                        //cambia in pop backstack
                        findNavController().navigate(R.id.reservationsFragment)
                        true
                    }
                    // Edit
                    R.id.edit_menu_item -> {
                        val action=ReservationXFragmentDirections.openReservationEdit(reservationId)
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
        builder.setPositiveButton("OK") { dialog, id ->
            // User clicked OK button
        }
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

    fun bind(s: ServiceWithFee){
        cBox.text=s.service.name+ "\n" +s.fee.toString()+"€"
    }
}


class MyAdapterRecycle1( private val l :List<ServiceWithFee>) : RecyclerView.Adapter<MyViewHolder1>(){

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
