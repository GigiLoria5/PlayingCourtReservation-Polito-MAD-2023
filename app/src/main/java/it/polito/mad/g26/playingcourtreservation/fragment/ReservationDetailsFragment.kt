package it.polito.mad.g26.playingcourtreservation.fragment


import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import it.polito.mad.g26.playingcourtreservation.model.Service
import it.polito.mad.g26.playingcourtreservation.model.custom.ServiceWithFee
import it.polito.mad.g26.playingcourtreservation.ui.CustomTextView
import it.polito.mad.g26.playingcourtreservation.util.setupActionBar
import it.polito.mad.g26.playingcourtreservation.viewmodel.ReservationWithDetailsVM


class ReservationDetailsFragment : Fragment(R.layout.fragment_reservation_details) {

    private val args: ReservationDetailsFragmentArgs by navArgs()
    private val reservationWithDetailsVM by viewModels<ReservationWithDetailsVM>()
    private lateinit var servicesAll: List<ServiceWithFee>
    private lateinit var servicesUsed: List<Service>
    private lateinit var servicesChosen: List<ServiceWithFee>
    private val dummyService = Service()
    private val dummyServiceWithFee = ServiceWithFee(dummyService, 0.0f)
    private val dummyListServiceWithFee = listOf(dummyServiceWithFee)
    private val today = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(activity, "Reservation Details", true)

        //List of text
        val center = view.findViewById<CustomTextView>(R.id.center_name)
            .findViewById<TextView>(R.id.value)
        val field = view.findViewById<CustomTextView>(R.id.court_name)
            .findViewById<TextView>(R.id.value)
        val sport = view.findViewById<CustomTextView>(R.id.sport)
            .findViewById<TextView>(R.id.value)
        val city = view.findViewById<CustomTextView>(R.id.city)
            .findViewById<TextView>(R.id.value)
        val address = view.findViewById<CustomTextView>(R.id.address)
            .findViewById<TextView>(R.id.value)
        val price = view.findViewById<CustomTextView>(R.id.price)
            .findViewById<TextView>(R.id.value)
        val date = view.findViewById<CustomTextView>(R.id.date)
            .findViewById<TextView>(R.id.value)
        val time = view.findViewById<CustomTextView>(R.id.time)
            .findViewById<TextView>(R.id.value)

        // Retrieve Reservation Details
        val reservationId = args.reservationId
        reservationWithDetailsVM
            .getReservationWithDetailsById(reservationId)
            .observe(viewLifecycleOwner) { reservation ->
                servicesUsed = reservation.services
                center.text = reservation.courtWithDetails.sportCenter.name
                field.text = reservation.courtWithDetails.court.name
                sport.text = reservation.courtWithDetails.sport.name
                city.text = reservation.courtWithDetails.sportCenter.city
                address.text = reservation.courtWithDetails.sportCenter.address
                date.text = reservation.reservation.date
                time.text = reservation.reservation.time
                price.text = reservation.reservation.amount.toString()

                //List of variable
                val dateList = reservation.reservation.date.split("-")
                val yearRes = dateList[2].toInt()
                val monthRes = dateList[1].toInt()
                val dayRes = dateList[0].toInt()
                val timeList = reservation.reservation.time.split(":")
                val hourRes = timeList[0].toInt()
                val calendarRes = Calendar.getInstance().apply {
                    set(Calendar.YEAR, yearRes)
                    set(Calendar.MONTH, monthRes - 1)
                    set(Calendar.DAY_OF_MONTH, dayRes)
                    set(Calendar.HOUR_OF_DAY, hourRes)
                    set(Calendar.MINUTE, 0)
                }

                //Alert Dialog
                val builder = AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
                builder.setMessage("Are you sure you want to delete the reservation?")
                builder.setPositiveButton("Yes") { _, _ ->
                    // User clicked OK button
                    reservationWithDetailsVM.deleteReservationById(reservationId)
                    findNavController().popBackStack()
                }
                builder.setNegativeButton("No") { _, _ ->
                    // User cancelled the dialog
                }

                //Button for modify and delete
                val modifyButton = view.findViewById<MaterialButton>(R.id.modify_reservation_button)
                val deleteButton = view.findViewById<Button>(R.id.delete_reservation_button)
                if (today <= calendarRes) {
                    deleteButton.setOnClickListener {
                        builder.show()
                    }
                    modifyButton.setOnClickListener {
                        val action =
                            ReservationDetailsFragmentDirections.openReservationEdit(reservationId)
                        findNavController().navigate(action)
                    }

                } else {
                    deleteButton.isEnabled = false
                    modifyButton.isEnabled = false
                    deleteButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                    modifyButton.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                }

                reservationWithDetailsVM.getAllServicesWithFee(reservation.courtWithDetails.sportCenter.id)
                    .observe(viewLifecycleOwner) { listOfServicesWithSportCenter ->
                        //List of services with fee of the sport center

                        reservationWithDetailsVM.getAllServices()
                            .observe(viewLifecycleOwner) { listService ->
                                //List of all services

                                //List of ServiceWithFee of that Sport center
                                servicesAll = reservationWithDetailsVM.allServiceWithoutSport(
                                    listOfServicesWithSportCenter,
                                    listService
                                )

                                //Filter service to take only chosen
                                servicesChosen = reservationWithDetailsVM.filterServicesWithFee(
                                    servicesAll,
                                    servicesUsed
                                )

                                //Recycler view of services
                                val recyclerView =
                                    view.findViewById<RecyclerView>(R.id.service_list)
                                if (servicesChosen.isEmpty())
                                    recyclerView.adapter =
                                        MyAdapterRecycle1(dummyListServiceWithFee, true)
                                else
                                    recyclerView.adapter = MyAdapterRecycle1(servicesChosen, false)
                                recyclerView.layoutManager = GridLayoutManager(context, 2)
                            }
                    }
            }


        /*MENU ITEM*/
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.reservation_details, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    // Back
                    android.R.id.home -> {
                        findNavController().popBackStack()
                        true
                    }

                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}

//Class to contain the view of a single random item
class MyViewHolder1(v: View) : RecyclerView.ViewHolder(v) {

    private val cBox = v.findViewById<MaterialButton>(R.id.material_button)

    fun bind(s: ServiceWithFee, empty: Boolean) {
        if (empty)
            cBox.text = super.itemView.context.getString(R.string.no_services_chosen)
        else
            cBox.text = super.itemView.context.getString(
                R.string.reservation_info_concatenation,
                s.service.name,
                s.fee.toString()
            )
    }
}

class MyAdapterRecycle1(private val l: List<ServiceWithFee>, private val empty: Boolean) :
    RecyclerView.Adapter<MyViewHolder1>() {

    //Inflater of the parent transform the xml of a row of the recyclerView into a view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder1 {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.service_recycler, parent, false)
        return MyViewHolder1(v)
    }

    //Need to know the max value of position
    override fun getItemCount(): Int {
        return l.size
    }

    //called after viewHolder are created, to put data into them
    override fun onBindViewHolder(holder: MyViewHolder1, position: Int) {
        holder.bind(l[position], empty)
    }
}
