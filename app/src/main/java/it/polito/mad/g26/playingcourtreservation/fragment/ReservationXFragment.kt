package it.polito.mad.g26.playingcourtreservation.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.g26.playingcourtreservation.R



class ReservationXFragment : Fragment(R.layout.fragment_reservation_x){

    private lateinit var spinnerSer: Spinner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var serviceUsed=getResources().getStringArray(R.array.services_array)
        var serviceAll=listOf("Bathroom","Var Usage","First Aid Kit","Supporters","MiniBar", "CheatMode")

        //Set spinner and adapter
        spinnerSer = view.findViewById(R.id.spinner)
        val adapterSer= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, serviceUsed)
        spinnerSer.adapter=adapterSer

        spinnerSer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                spinnerSer.setSelection(0)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        //Crate popup window
        val popupView = layoutInflater.inflate(R.layout.popup_window, null)
        val popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        //Set up recycler view in the popup window
        val recyclerView = popupView.findViewById<RecyclerView>(R.id.recycler_list)
        recyclerView.adapter = AdapterRecycle(serviceAll)

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        val dismissButton=popupView.findViewById<Button>(R.id.button_dismiss)
        dismissButton.setOnClickListener{
            popupWindow.dismiss()
        }

        val servicesButton=view.findViewById<Button>(R.id.ReservationModify)
        servicesButton.setOnClickListener{
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        }

    }

}

class HolderRecycle(v:View):RecyclerView.ViewHolder(v){
    val tv=v.findViewById<TextView>(R.id.item_of_list_service)

}

class AdapterRecycle( val l :List<String>) : RecyclerView.Adapter<HolderRecycle>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecycle {
        val v=LayoutInflater.from(parent.context).inflate(R.layout.recycler_item,parent,false)
        return HolderRecycle(v)
    }

    override fun getItemCount(): Int {
        return l.size
    }

    override fun onBindViewHolder(holder: HolderRecycle, position: Int) {
        holder.tv.text=l[position]
    }
}