package it.polito.mad.g26.playingcourtreservation.fragment

import android.icu.util.Calendar
import android.os.Bundle
import android.view.*
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
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import it.polito.mad.g26.playingcourtreservation.R
import it.polito.mad.g26.playingcourtreservation.activity.MainActivity
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.util.ReservationWithDetailsUtil
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM
import it.polito.mad.g26.playingcourtreservation.viewmodel.searchFragments.SearchCourtResultsVM


class ModifyReservationXFragment : Fragment(R.layout.fragment_modify_reservation_x) {


    /*   VISUAL COMPONENTS       */
    private lateinit var dateMCV: MaterialCardView
    private lateinit var dateTV: TextView
    private lateinit var hourMCV: MaterialCardView
    private lateinit var hourTV: TextView

    private val reservationDetailsUtil = ReservationWithDetailsUtil
    private val vm by viewModels<SearchCourtResultsVM>()


    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()
    private lateinit var servicesUsed : List<Service>
    private lateinit var servicesAll : List<ServiceWithFee>
    private lateinit var servicesChoosen : MutableList<ServiceWithFee>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var amount=mutableListOf(0.0f)
        var centerOpenTime :Int =16
        var centerCloseTime: Int=20
        var dateDayReservation = Calendar.getInstance()

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
        dateTV = view.findViewById(R.id.dateTV)
        hourTV = view.findViewById(R.id.hourTV)
        val priceNew=view.findViewById<CustomTextView>(R.id.price_new)
            .findViewById<TextView>(R.id.value)
        val dateNew=view.findViewById<CustomTextView>(R.id.date_new)
            .findViewById<TextView>(R.id.value)
        val timeNew=view.findViewById<CustomTextView>(R.id.time_new)
            .findViewById<TextView>(R.id.value)

        // Retrieve Reservation Details
        val reservationId = args.reservationId
        reservationWithDetailsVM
            .getReservationWithDetailsById(reservationId)
            .observe(viewLifecycleOwner) { reservation ->

                //Compile TextView
                center.text=reservation.courtWithDetails.sportCenter.name
                field.text=reservation.courtWithDetails.court.name
                sport.text=reservation.courtWithDetails.sport.name
                city.text=reservation.courtWithDetails.sportCenter.city
                address.text=reservation.courtWithDetails.sportCenter.address
                date.text=reservation.reservation.date
                time.text=reservation.reservation.time
                price.text=reservation.reservation.amount.toString()
                amount=mutableListOf(reservation.reservation.amount)
                dateNew.text=reservation.reservation.date
                timeNew.text=reservation.reservation.time
                priceNew.text=reservation.reservation.amount.toString()

                //Variables
                dateDayReservation=reservationWithDetailsVM.createCalendarObject(reservation.reservation.date,reservation.reservation.time)
                centerOpenTime=reservationWithDetailsVM.takeIntCenterTime(reservation.courtWithDetails.sportCenter.openTime)
                centerCloseTime=reservationWithDetailsVM.takeIntCenterTime(reservation.courtWithDetails.sportCenter.closeTime)

                //Select date of reservation as initial date
                reservationWithDetailsVM.changeSelectedDateTimeMillis(dateDayReservation.timeInMillis)

                //List of service used
                servicesUsed=reservation.services

                reservationWithDetailsVM.getAllServicesWithFee(reservation.courtWithDetails.sportCenter.id)
                    .observe(viewLifecycleOwner){listOfServicesWithSportCenter->
                        //List of services with fee of the sport center
                        val c=listOfServicesWithSportCenter

                        reservationWithDetailsVM.getAllServices().observe(viewLifecycleOwner){listService->
                            //List of all services
                            val d=listService

                            //List of ServiceWithFee of that SportCenter
                            servicesAll=reservationWithDetailsVM.allServiceWithoutSport(c,d)

                            //List of service chosen
                            servicesChoosen=reservationWithDetailsVM.filterServicesWithFee(servicesAll,servicesUsed).toMutableList()

                            //Recycler view of services
                            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_chip)
                            recyclerView.adapter=MyAdapterRecycle(servicesAll,servicesChoosen,priceNew,amount)
                            recyclerView.layoutManager= GridLayoutManager(context,2)

                        }

                    }

            }

        /*ALERT DIALOG FOR SUCCESSFUL UPDATE*/
        val builderUpdated = AlertDialog.Builder(requireContext(),R.style.MyAlertDialogStyle)
        builderUpdated.setMessage("The reservation has been update successfully")
        builderUpdated.setPositiveButton("Ok") { _, _ ->
            // User clicked OK button
        }

        /*MENU CUSTOMIZATION*/
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
                        println("questi sono i nuovi services")
                        println(servicesChoosen)

                        //Take Ids of services
                        var idsServices=reservationWithDetailsVM.getListOfIdService(servicesChoosen)
                        println("Questi sono gli Id di prima")
                        println(idsServices)

                        //Date changes
                        var date=reservationWithDetailsVM.changeDateToFull(dateTV.text.toString())
                        reservationWithDetailsVM.updateReservation(date,hourTV.text.toString(),reservationId,idsServices,amount[0])
                        builderUpdated.show()
                        //navigate back because id will be the same
                        findNavController().popBackStack()
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





        /* DATE MATERIAL CARD VIEW MANAGEMENT*/
        dateMCV = view.findViewById(R.id.dateMCV)
        dateMCV.setOnClickListener {
            reservationDetailsUtil.showDatePickerDialog(
                requireContext(),
                reservationWithDetailsVM,
                reservationId,
                hourTV,
                viewLifecycleOwner,
                dateNew
            )
        }

        /* HOUR MATERIAL CARD VIEW MANAGEMENT*/
        hourMCV = view.findViewById(R.id.hourMCV)
        hourMCV.setOnClickListener {
            reservationDetailsUtil.showNumberPickerDialog(
                requireContext(),
                reservationWithDetailsVM,
                centerOpenTime,
                centerCloseTime,
                reservationId,
                dateTV,
                viewLifecycleOwner,
                timeNew
            )

        }

        reservationWithDetailsVM.selectedDateTimeMillis.observe(viewLifecycleOwner) {
            //never used c?
            val c = Calendar.getInstance()
            c.timeInMillis = vm.selectedDateTimeMillis.value!!
            reservationDetailsUtil.setDateTimeTextViews(
                reservationWithDetailsVM.selectedDateTimeMillis.value ?:0,
                getString(R.string.dateFormat),
                getString(R.string.hourFormat),
                dateTV,
                hourTV
            )
        }







    }

}




//Class to contain the view of a single random item
class MyViewHolder (v:View) : RecyclerView.ViewHolder(v){

    private val chip=v.findViewById<Chip>(R.id.chip)

    fun bind(s: ServiceWithFee, lUsed : MutableList<ServiceWithFee>, price: TextView, amount:MutableList<Float> ){


        chip.text=s.service.name+" " + s.fee +"€"
        chip.isChecked = s in lUsed
        chip.isCloseIconVisible = chip.isChecked
        chip.setOnClickListener {
            chip.isCloseIconVisible = chip.isChecked
            if (chip.isChecked) {
                lUsed.add(s)
                amount[0]+=s.fee
                price.text = amount[0].toString()



            }else {
                lUsed.remove(s)
                amount[0] -= s.fee
                price.text = amount[0].toString()
            }
        }
    }


    fun unbind() {
        chip.text = ""
        chip.setOnClickListener(null)
    }
}




//class that uses the viewHolder to show a specific item
class MyAdapterRecycle( val l :List<ServiceWithFee>, var lUsed : MutableList<ServiceWithFee>, var price :TextView, var amount:MutableList<Float>) : RecyclerView.Adapter<MyViewHolder>(){

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
        holder.bind(l[position],lUsed,price,amount)
    }
}

